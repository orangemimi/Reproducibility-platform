<template>
  <div id="leftHistory">
    <!-- <el-scrollbar style="width: 100%; height: 100%"> -->
    <el-card class="leftCard">
      <h3><strong>History Documents</strong></h3>
      <br />

      <el-button
        size="small"
        round
        type="warning"
        @click="replaceDialogShow = true"
        title="This document will overwrite the original document"
        >Replace
      </el-button>
      <el-button
        size="small"
        round
        type="primary"
        @click="saveAsDialogShow = true"
        title="You can save as a new document"
        >Save As</el-button
      >

      <el-dialog
        title="Replace the original document"
        v-model="replaceDialogShow"
        style="margin-top: 200px"
      >
        <div>
          <span>
            <h5 style="color: red">
              Tip: You can only save it as a .xlsx file!
            </h5>
          </span>
          <br />
          <span>
            <h3>NewFileName(Excluding extension name):</h3>
            <br />
            <el-input disabled :placeholder="currentDocument.name">
              <template #append>.xlsx</template>
            </el-input>
          </span>
          <br />
          <br />
          <span>
            <h3>Notes:</h3>
            <br />
            <el-input
              v-model="replaceDocumentNotes"
              placeholder="Enter notes about this data"
              type="textarea"
              rows="2"
            ></el-input>
          </span>
        </div>

        <template v-slot:footer>
          <span class="dialog-footer">
            <el-button @click="handleClose">Cancel</el-button>
            <el-button type="primary" @click="replaceOldDocument">
              Save
            </el-button>
          </span>
        </template>
      </el-dialog>

      <el-dialog title="Save as a new document" v-model="saveAsDialogShow">
        <div>
          <span>
            <h5 style="color: red">
              Tip: You can only save it as a .xlsx file!
            </h5>
          </span>
          <br />
          <span>
            <h3>NewFileName(Excluding extension name):</h3>
            <br />
            <el-input v-model="SaveNewDocumentName">
              <template #append>.xlsx</template>
            </el-input>
          </span>
          <br />
          <br />
          <span>
            <h3>Notes:</h3>
            <br />
            <el-input
              v-model="SaveNewDocumentNotes"
              placeholder="Enter notes about this data"
              type="textarea"
              rows="2"
            ></el-input>
          </span>
        </div>

        <template v-slot:footer>
          <span class="dialog-footer">
            <el-button @click="handleClose">Cancel</el-button>
            <el-button type="primary" @click="saveAsNewDocument">
              Save
            </el-button>
          </span>
        </template>
      </el-dialog>

      <el-divider></el-divider>
      <div class="historyContainer">
        <el-card
          v-for="(item, index) in historyList"
          :key="index"
          class="historyCard"
          @click="readDocument(item)"
        >
          <p :title="item.updateTime">{{ item.updateTime }}</p>
          <p :title="item.notes">Notes:{{ item.notes || "No remarks" }}</p>
        </el-card>
      </div>
    </el-card>
    <!-- </el-scrollbar> -->
  </div>
  <div id="luckysheet" class="luckySheet"></div>
  <div v-show="isMaskShow" id="tip">Loading online documents</div>
</template>
  
<script setup>
// import '../../assets/formOL/plugin.js';
// import '../../assets/formOL/luckysheet.umd.js';

import { ref, onMounted, defineProps, watch } from "vue";
import { exportExcel } from "./export";
import LuckyExcel from "luckyexcel";
import Papa from "papaparse";
import * as XLSX from "xlsx";
import { ElMessage } from "element-plus";
import axios from "axios";
import { getDataItems, saveData, getFolderIdByDataItemId , saveDocument ,replaceDocument } from "@/api/request";
import { renderSize } from '@/utils/utils'


/** 全局参数与通用方法 */
// 加载遮蔽动画的布尔值
const isMaskShow = ref(false);

// 如果是csv文件，则将其数据先转化为json
const jsonData = ref({});

const handleClose = () => {
  replaceDialogShow.value = false;
  saveAsDialogShow.value = false;
};

const currentDocument = ref({});
/** sheet基础功能实现 */
// 点击历史文档，下载和转化对应的文件，生成file对象
const readDocument = async (currentRow) => {
  currentDocument.value = currentRow;
  isMaskShow.value = true;
  console.log(currentRow, "201");
  try {
    // 发送 HTTP 请求获取 Excel 文件的二进制数据
    const response = await axios.get(currentRow.value, {
      responseType: "arraybuffer",
    });

    // 获取 Content-Disposition 头部中的文件名
    const fileName = response.headers
      .get("content-disposition")
      .split("fileName=")[1];

    // 直接创建 Blob 和 File 对象
    const blob = new Blob([response.data], {
      type: response.headers["content-type"],
    });
    const file = new File([blob], fileName, {
      type: response.headers["content-type"],
    });

    loadExcel(file);
    isMaskShow.value = false;
  } catch (error) {
    console.error("Error fetching or processing Excel file:", error);
    // 在实际应用中，你可能需要进一步处理错误，例如提示用户或者进行其他操作
  }
};

// 载入文件，并重新加载sheet
const loadExcel = async (evt) => {
  var files = await judgeExcelType(evt);
  LuckyExcel.transformExcelToLucky(
    files,
    function (exportJson, luckysheetfile) {
      if (exportJson.sheets == null || exportJson.sheets.length == 0) {
        alert(
          "Failed to read the content of the excel file, currently does not support xls files!"
        );
        return;
      }
      // console.log("exportJson", exportJson);
      jsonData.value = exportJson;

      window.luckysheet.destroy();

      window.luckysheet.create({
        container: "luckysheet", //luckysheet is the container id
        showinfobar: false,
        data: exportJson.sheets,
        title: exportJson.info.name,
        userInfo: exportJson.info.name.creator,
      });
    }
  );
};

// 判断文件类型（在线下载数据时需要用的方法）
const judgeExcelType = (evt) => {
  // var files = evt.target.files[0];
  // var files = evt.target.files;
  var files = evt;
  var newFile;
  // console.log(files, "file");
  if (files == null || files.length == 0) {
    ElMessage({
      type: "warning",
      message: "No files wait for import",
    });
    return;
  }

  let name = files.name;
  let suffixArr = name.split("."),
    suffix = suffixArr[suffixArr.length - 1];
  if (suffix != "xlsx" && suffix != "csv" && suffix != "xls") {
    ElMessage({
      type: "warning",
      message: "Currently only supports the import of xlsx or csv files",
    });
    return;
  }
  if (suffix == "csv") {
    // csv to xlsx
    newFile = csvToXlsx(files);
    return newFile;
  } else if (suffix == "xls") {
    // xls to xlsx
    // newFile = xslToXlsx();
    ElMessage({
      type: "warning",
      message: "Temporarily does not support .xls files",
    });
    // console.log(newFile, "456");
    // return newFile;
  }
  return files;
};

// 二进制数据转xlsx，有必要再写，从csv里面搬出来就行
const bufferToXlsx = (buffer) => {};

// 如果文件是csv，就转化为Xlsx
const csvToXlsx = async (file) => {
  const reader = new FileReader();

  const result = await new Promise((resolve, reject) => {
    reader.onload = (event) => {
      const csvData = event.target.result;

      // 解析CSV数据
      const parsedData = Papa.parse(csvData, { header: true });
      const csvRows = parsedData.data;

      // 创建一个工作簿
      const workbook = XLSX.utils.book_new();

      // 创建一个工作表
      const worksheet = XLSX.utils.json_to_sheet(csvRows);
      // 将工作表添加到工作簿
      XLSX.utils.book_append_sheet(workbook, worksheet, "Sheet 1");
      try {
        // 尝试将工作簿保存为XLSX文件
        const xlsxBuffer = XLSX.write(workbook, {
          bookType: "xlsx",
          type: "buffer",
        });

        const xlsxFile = new File([xlsxBuffer], "test.xlsx", {
          type: "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        });

        resolve(xlsxFile);
      } catch (error) {
        reject(error);
      }
    };

    reader.onerror = () => {
      reject(new Error("Failed to read the file."));
    };

    reader.readAsText(file);
  });

  return result;
};

// 还没写，暂无该需求
const xslToXlsx = () => {};

/** 保存功能实现 */
//  保存和另存功能

/** 保存（覆盖） */
const replaceDocumentNotes = ref("");
const replaceDialogShow = ref(false);

const replaceOldDocument = async () => {
  var file = await exportExcel(luckysheet.getAllSheets(), `${currentDocument.value.name}.xlsx`);
  let uploadFileForm = new FormData();
  uploadFileForm.append("file", file);
  var historyId = [];
  historyId.push(currentDocument.value.id);
  for(var id of currentDocument.value.history){
    historyId.push(id);
  }
  let notes = replaceDocumentNotes.value;
  uploadFileForm.append("history", historyId);
  uploadFileForm.append("notes", notes);

  let data = await replaceDocument(
    uploadFileForm,
    renderSize(file.size),
    await getFolderIdByDataItemId(currentDocument.value.id),
  )
  if(data){
    replaceDialogShow.value = false;
    ElMessage({
      'type':'success',
      'message':"Replacement successful"
    })
  }
};

/** 另存（新建） */
const SaveNewDocumentNotes = ref("");
const SaveNewDocumentName = ref("");
const saveAsDialogShow = ref(false);

const saveAsNewDocument = async () => {
  var file = await exportExcel(luckysheet.getAllSheets(), `${SaveNewDocumentName.value}.xlsx`);
  let uploadFileForm = new FormData();
  uploadFileForm.append("file", file);
  var historyId = [];
  historyId.push(currentDocument.value.id);
  for(var id of currentDocument.value.history){
    historyId.push(id);
  }
  let notes = SaveNewDocumentNotes.value;
  uploadFileForm.append("history", historyId);
  uploadFileForm.append("notes", notes);
  let data = await saveDocument(
    uploadFileForm,
    renderSize(file.size),
    await getFolderIdByDataItemId(currentDocument.value.id),
  )
  if(data){
    saveAsDialogShow.value = false;
    ElMessage({
      'type':'success',
      'message':"Save as successful"
    })
  }
};

/** 历史文档功能实现 */
// 历史文档记录
const historyList = ref([]);

// 父组件传递的值，点击的具体文档
const props = defineProps(["currentRow"]);

// 数据梳理，将点击行的基础信息、history中的基础信息梳理出来
const getDocumentsData = async (rowData) => {
  historyList.value = [];
  // console.log(rowData,'rowData');
  var historyId = [];
  var document = {};
  historyId = rowData.history;
  document = rowData;
  historyList.value.push(document);
  if (historyId) {
    var historyDocuments = await getDataItems(historyId);
    // console.log(historyDocuments,'historyDocuments');
    historyDocuments.forEach((element) => {
      historyList.value.push(element);
    });
  }
};

/** sheet的初始化与销毁重构 */

// 初始化方法，包括了sheet组件的销毁
const initSheet = async (data) => {
  luckysheet.destroy({
    container: "luckysheet",
  });
  await getDocumentsData(data);
  readDocument(historyList.value[0]);
  var sheetTitle = historyList?.value[0]?.name
    ? historyList.value[0].name
    : "Unnamed Sheet";
  luckysheet.create({
    container: "luckysheet",
    title: sheetTitle, // 设定表格名称
    lang: "zh", // 设定表格语言
  });
};

// 如果更换选定的文件，将新选定的内容作为传入值重新初始化一遍
watch(
  () => props.currentRow,
  (newValue, oldValue) => {
    initSheet(newValue);
  }
);

// !!! create luckysheet after mounted
onMounted(async () => {
  // 加载首页时载入数据
  if (props.currentRow) {
    initSheet(props.currentRow);
  } else {
    luckysheet.create({
      container: "luckysheet",
      title: "luckysheet", // 设定表格名称
      lang: "zh", // 设定表格语言
    });
  }
});
</script>
  
<style  scoped lang="scss">
#luckysheet {
  margin: 0px;
  padding: 0px;
  position: absolute;
  width: 87.8%;
  left: 12%;
  top: 60px;
  bottom: 5px;
}

#uploadBtn {
  font-size: 16px;
}

#tip {
  position: absolute;
  z-index: 1000000;
  left: 0px;
  top: 0px;
  bottom: 0px;
  right: 0px;
  background: rgba(255, 255, 255, 0.8);
  text-align: center;
  font-size: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
}
#leftHistory {
  margin: 0px;
  padding: 0px;
  position: absolute;
  /* 留点缝更好看 */
  width: 11.7%;
  height: calc(100% - 60px);
  left: 3px;
  top: 60px;
  bottom: 5px;
  display: flex;
  flex-flow: wrap;
}
.leftCard {
  width: 100%;
  height: 100%;
  overflow-y: auto;
}
.historyCard {
  width: 100%;
  height: 20%;
  margin-top: 5px;
  cursor: pointer;
  line-height: 1.5;
  overflow: hidden;
  white-space: nowrap;
  text-overflow: ellipsis;
  :hover {
    background-color: #f0f0f0;
  }
}
.historyContainer {
  max-height: 100%;
  /* height: 60vh; */
}

:deep(.el-popup-parent--hidden) {
  .luckysheet-wa-editor {
    z-index: 999999;
  }
}
// :deep(.el-scrollbar__view){
//   height:100%;
// }
</style>
  