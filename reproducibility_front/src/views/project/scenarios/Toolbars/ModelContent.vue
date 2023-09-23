<!--  -->
<template>
  <div>
    <div class="main">
      <el-row class="title">
        <el-col>{{ modelItem.name }}</el-col>
      </el-row>

      <el-row>
        <el-col :span="17">
          <p class="description">{{ modelItem.description }}</p>
        </el-col>
        <el-col :span="7">
          <el-button
            type="success"
            size="mini"
            style="float:right"
            @click="getInstances"
          >
            <i class="el-icon-setting"></i>&nbsp;Instances
          </el-button>
          <el-button
            type="primary"
            size="mini"
            @click="invokeTest"
            style="float:right;margin-right:5px"
          >
            <i class="el-icon-setting"></i>&nbsp;Invoke
          </el-button>
        </el-col>
      </el-row>

      <el-collapse
        v-model="activeNames"
        @change="handleChange"
        v-for="(state, index) in modelItem.behavior"
        :key="index"
      >
        <el-collapse-item :title="state.name" :name="index">
          <!-- <el-divider></el-divider> -->
          <div>
            <div class="_params-group">
              <el-row v-if="state.inputs.length !== 0" class="stateTitle"
                >Inputs</el-row
              >
              <el-divider class="stateTitleDivider"></el-divider>
              <div class="events">
                <el-row
                  v-for="(modelInEvent, inEventIndex) in state.inputs"
                  :key="inEventIndex"
                  class="event"
                >
                  <el-row>
                    <el-col :span="17" class="_event-desc">
                      <span class="event_name" :title="modelInEvent.name">
                        <span
                          v-show="modelInEvent.isOptional == false"
                          style="color: red"
                          >*</span
                        >
                        {{ modelInEvent.name }}
                      </span>
                      <p class="event_desc" :title="modelInEvent.description">
                        {{ modelInEvent.description }}
                      </p>
                    </el-col>

                    <el-row>
                      <div
                        v-if="
                          modelInEvent.hasOwnProperty('url') &&
                            modelInEvent.url != '' &&
                            modelInEvent.urlName != ''
                        "
                      >
                        <div class="select-data select-data-line">
                          <div class="data-name">
                            {{ modelInEvent.urlName }}
                          </div>
                          <el-button
                            type="success"
                            icon="el-icon-download"
                            size="mini"
                            circle
                            @click="download(modelInEvent)"
                          ></el-button>
                          <el-button
                            type="warning"
                            icon="el-icon-close"
                            size="mini"
                            circle
                            @click="remove(modelInEvent)"
                          ></el-button>
                        </div>
                      </div>
                      <div v-else>
                        <el-button
                          type="primary"
                          plain
                          @click="selectDataDialog(modelInEvent)"
                          v-if="!isDataSelected(modelInEvent)"
                        >
                          Select data
                        </el-button>
                        <el-button
                          type="success"
                          plain
                          @click="clearData(modelInEvent)"
                          v-else
                        >
                        Delete {{getFileName(modelInEvent)}} &#10006;
                        </el-button>
                      </div>
                    </el-row>
                  </el-row>
                  <el-row>
                    <el-divider class="eventDivider"></el-divider>
                  </el-row>
                </el-row>
              </div>
            </div>

            <div class="_params-group">
              <el-row v-if="state.parameters.length !== 0" class="stateTitle"
                >Parameters</el-row
              >
              <el-divider class="stateTitleDivider"></el-divider>
              <div class="events">
                <el-row
                  v-for="(modelInEvent, inEventIndex) in state.parameters"
                  :key="inEventIndex"
                  class="event"
                >
                  <el-row>
                    <el-col :span="17" class="_event-desc">
                      <span class="event_name" :title="modelInEvent.name">
                        <span
                          v-show="modelInEvent.isOptional == false"
                          style="color: red"
                          >*</span
                        >
                        {{ modelInEvent.name }}
                      </span>
                      <p class="event_desc" :title="modelInEvent.description">
                        {{ modelInEvent.description }}
                      </p>
                    </el-col>

                    <el-row
                      v-if="
                        Object.prototype.hasOwnProperty.call(
                          modelInEvent,
                          'datasetItem'
                        ) && modelInEvent.datasetItem.type == `internal`
                      "
                    >
                      <div v-if="filterUdxNode(modelInEvent)">
                        <el-table
                          border
                          :data="filterUdxNode(modelInEvent)[0].UdxNode"
                        >
                          <el-table-column
                            prop="name"
                            label="Parameter"
                            width="180"
                          ></el-table-column>
                          <el-table-column
                            prop="description"
                            label="Description"
                            width="180"
                          ></el-table-column>
                          <el-table-column
                            prop="type"
                            label="Type"
                          ></el-table-column>
                          <el-table-column label="Value">
                            <template slot-scope="scope">
                              <el-input v-model="scope.row.value"></el-input>
                            </template>
                          </el-table-column>
                        </el-table>
                      </div>
                      <div v-else>
                        <div
                          v-if="
                            modelInEvent.hasOwnProperty('url') &&
                              modelInEvent.url != '' &&
                              modelInEvent.urlName != ''
                          "
                        >
                          <div class="select-data select-data-line">
                            <div class="data-name">
                              {{ modelInEvent.urlName }}
                            </div>
                            <el-button
                              type="success"
                              icon="el-icon-download"
                              size="mini"
                              circle
                              @click="download(modelInEvent)"
                            ></el-button>
                            <el-button
                              type="warning"
                              icon="el-icon-close"
                              size="mini"
                              circle
                              @click="remove(modelInEvent)"
                            ></el-button>
                          </div>
                        </div>
                        <div v-else>
                          <el-button
                          type="primary"
                          plain
                          @click="selectDataDialog(modelInEvent)"
                          v-if="!isDataSelected(modelInEvent)"
                          >
                            Select data
                          </el-button>
                          <el-button
                            type="success"
                            plain
                            @click="clearData(modelInEvent)"
                            v-else
                          >
                          Delete {{getFileName(modelInEvent)}} &#10006;
                          </el-button>
                        </div>
                      </div>
                    </el-row>
                    <el-row v-else>
                      <div
                        v-if="
                          modelInEvent.hasOwnProperty('url') &&
                            modelInEvent.url != '' &&
                            modelInEvent.urlName != ''
                        "
                      >
                        <div class="select-data select-data-line">
                          <div class="data-name">
                            {{ modelInEvent.urlName }}
                          </div>
                          <el-button
                            type="success"
                            icon="el-icon-download"
                            size="mini"
                            circle
                            @click="download(modelInEvent)"
                          ></el-button>
                          <el-button
                            type="warning"
                            icon="el-icon-close"
                            size="mini"
                            circle
                            @click="remove(modelInEvent)"
                          ></el-button>
                        </div>
                      </div>
                      <div v-else>
                        <el-button
                          type="primary"
                          plain
                          @click="selectDataDialog(modelInEvent)"
                          v-if="!isDataSelected(modelInEvent)"
                        >
                          Select data
                        </el-button>
                        <el-button
                          type="success"
                          plain
                          @click="clearData(modelInEvent)"
                          v-else
                        >
                        Delete {{getFileName(modelInEvent)}} &#10006;
                        </el-button>
                      </div>
                    </el-row>
                  </el-row>
                  <el-row>
                    <el-divider class="eventDivider"></el-divider>
                  </el-row>
                </el-row>
              </div>
            </div>

            <div class="_params-group">
              <el-row v-if="state.outputs.length !== 0" class="stateTitle"
                >Output</el-row
              >
              <el-divider class="stateTitleDivider"></el-divider>
              <div class="events">
                <el-row
                  v-for="(modelOutEvent, outEventIndex) in state.outputs"
                  :key="outEventIndex"
                  class="event"
                >
                  <el-row>
                    <el-col :span="17" class="_event-desc">
                      <span class="event_name" :title="modelOutEvent.name">{{
                        modelOutEvent.name
                      }}</span>
                      <p class="event_desc" :title="modelOutEvent.description">
                        {{ modelOutEvent.description }}
                      </p>
                    </el-col>
                    <el-col :span="6" :offset="1">
                      <div class="_btn-group">
                        <el-button
                          plain
                          round
                          type="warning"
                          @click="download(modelOutEvent)"
                          v-if="
                            modelOutEvent.hasOwnProperty('url') &&
                              modelOutEvent.url != ''
                          "
                          >Download</el-button
                        >
                        <el-button
                          plain
                          round
                          type="warning"
                          @click="bind(modelOutEvent)"
                          :class="{ bindClass: modelOutEvent.bind }"
                          v-if="
                            modelOutEvent.hasOwnProperty('url') &&
                              modelOutEvent.url != ''
                          "
                          >Bind</el-button
                        >
                      </div>
                    </el-col>
                  </el-row>
                  <el-row>
                    <el-divider class="eventDivider"></el-divider>
                  </el-row>
                </el-row>
              </div>
            </div>
          </div>
        </el-collapse-item>
      </el-collapse>
    </div>
    <div class="selectData" >
      <el-dialog
        :visible.sync="instanceDialogShow"
        width="900px"
        title="Insatnces"
        :close-on-click-modal="false"
        :destroy-on-close="true"
      >
        <el-table
        :data="splitData"
        style="width: 100%;">
          <el-table-column type="expand">
            <template slot-scope="props">
              <h1>Model Configuration</h1><br><hr>
              <!-- {{ props.row }} -->
              <el-table
              :data="dataTable(props.row)"
              style="width: 100%;"
              :row-class-name="tableRowClassName">
                <el-table-column label="Name" prop="name">
                </el-table-column>
                <el-table-column label="Type" prop="type">
                </el-table-column>
                <el-table-column label="Description" prop="description">
                </el-table-column>
                <el-table-column label="Data Download or Parameters Value">
                  <template slot-scope="scope">
                    <div v-if="scope.row.type=='parameters'">Parameter Value：{{props.row.parameters[0].datasetItem.UdxDeclaration[0].UdxNode[0].UdxNode[0].value}}</div>
                    <el-button v-else type="info" round @click="download(scope.row,props.row)">
                    <i class="el-icon-download"></i>Download</el-button>
                  </template>
                
                </el-table-column>
              </el-table>
            </template>
          </el-table-column>
          <el-table-column label="name" prop="pname">
          </el-table-column>
          <el-table-column label="createTime" prop="createTime">
          </el-table-column>
          <el-table-column label="statusEnum" prop="status">
            <template slot-scope="scope">
              <el-tag
              :type="scope.row.status === 'success'? 'success':'info'"
              disable-transitions>{{ scope.row.status }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="Operation">
            <template slot-scope="props">
              <el-button
                v-if="props.row.status === 'success'"
                :type="getButtonType(props.row)" 
                round
                @click="toggleBindStatus(props.row)"
              >
                <i class="el-icon-check"></i>
                {{ getButtonText(props.row) }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- {{ modelInstanceList }} -->
      </el-dialog>
    </div>

    <div class="selectData">
      <el-dialog
        :visible.sync="selectDataDialogShow"
        width="900px" 
        title="Select data from resource center or upload"
        :close-on-click-modal="false"
        :destroy-on-close="true"
      >
        <resource-table @submitDataToEvent="submitDataToEvent" :splitDataProp="boundData" ></resource-table>
      </el-dialog>
    </div>
  </div>
</template>

<script>
import ResourceTable from "./ResourceTable.vue";
import {
  saveData,
  invokeSingleModel,
  initTask,
  saveInstance,
  updateInstance,
  getRecordofSingleModel,
  getInstancesInScenario,
  bindScenario,
  getScenarioById,
  getProjectById
  //   getInstanceById,
} from "@/api/request";
import { renderSize } from "@/utils/utils";
import { mapState } from "vuex";
import { dateFormat } from "@/lib/utils";
export default {
  props: {
    currentModel: {
      type: Object,
    },
    scenarioId: {
      type: String,
    },
    splitDataProp:{
      type:Array,
    }
  },
  components: { ResourceTable },

  watch: {
    currentModel: {
      async handler(newVal) {
        if (Object.hasOwnProperty.call(newVal, "md5")) {
          this.modelItem = newVal;
          await this.initTask();
        }
      },
      deep: true,
      immediate: true,
    },
    instanceDialogShow:{
      async handler(newVal){
        if(newVal === false&&this.modelInstanceList.length>0){
          let update = {'instances':this.boundInstances}
          let scenarioId = this.modelInstanceList[0].scenarioId;
          await bindScenario(scenarioId,update)
        }
      },
      deep: true,
      immediate: false,
    }
  },

  computed: {
    ...mapState({
      userId: (state) => state.user.userId,
    }),
    //把获取数据中的name字段分开
    splitData(){
      return this.modelInstanceList.map(item => {
        let [pname, createTime] = item.name.split("||");
        let { status, behavior,id, scenarioId} = item;
        if(status==-1){
          status = "failed";
        }else if(status==2){
          status = "success";
        }else{
          status = "running"
        }
        let inputs =[];
        let outputs =[];
        let parameters=[];
        //这里有问题，一个实例如果有多个state，那么如何显示呢？目前显示的字段并没有代表state的，这样会杂糅
        for(let i = 0 ; i<behavior.length ; i++){
          inputs  = [...inputs, ...behavior[i].inputs];
          outputs = [...outputs, ...behavior[i].outputs];
          parameters = [...parameters , ...behavior[i].parameters];
        }
        return {
          id,
          scenarioId,
          pname,
          createTime,
          status,
          behavior,
          inputs,
          outputs,
          parameters,
        };
      });
    },
    boundData(){
      if(this.scenario.instanceObjects != null){
        return this.scenario.instanceObjects.instanceList.map(item => {
          let [pname, createTime] = item.name.split("||");
          let { status, behavior,id, scenarioId} = item;
          status = ((status == 2) ? "success": (status == -1) ? "failed" : "running");
          let inputs =[];
          let outputs =[];
          let parameters=[];
          //这里有问题，一个实例如果有多个state，那么如何显示呢？目前显示的字段并没有代表state的，这样会杂糅
          for(let i = 0 ; i<behavior.length ; i++){
            inputs  = [...inputs, ...behavior[i].inputs];
            outputs = [...outputs, ...behavior[i].outputs];
            parameters = [...parameters , ...behavior[i].parameters];
          }
          return {
            id,
            scenarioId,
            pname,
            createTime,
            status,
            behavior,
            inputs,
            outputs,
            parameters,
          };
        });
      }
      return null
    },
  },

  data() {
    return {
      scenario:{},
      boundInstances:[],
      selectedDataName:'',
      selectedDataEvent:[],
      modelItem: this.currentEvent,
      modelInstance: {},
      modelInstanceList: [],
      instanceDialogShow: false,
      activeNames: 0,
      selectDataDialogShow: false,
      currentEvent: "",
      timer: {},
      recordOutput: {},
      refreshForm: {},
      invokeForm: {
        ip: "",
        port: "",
        pid: "",
        username: "",
        inputs: [
          {
            statename: "",
            event: "",
            url: "",
            tag: "",
          },
        ],
        outputs: [
          {
            statename: "",
            event: "",
            template: {
              type: "", //id|none
              value: "", //if tyoe=none value=""
            },
          },
        ],
      },
    };
  },

  methods: {
    // test(value){
    //   console.log(value,'value打印');
    // },
    getButtonType(row) {
      // 根据按钮的状态返回按钮类型
      return this.isBound(row) ? 'success' : 'primary';
    },
    getButtonText(row) {
      // 根据按钮的状态返回按钮文字
      return this.isBound(row) ? 'bound' : 'bind';
    },
    isBound(row) {
      // 检查按钮是否已绑定
      if(this.boundInstances != null){
        return this.boundInstances.includes(row.id);
      }
      return false
    },
    async toggleBindStatus(row) {
      if(row.status!=="success"){
        this.$message({
          showClose: true,
          message: 'Please bind the instance that ran successfully',
          type: 'error'
        });
      }else{
        // 切换按钮的状态
        if (this.isBound(row)) {
          // 执行解绑逻辑
          this.unBindInstanceId(row);
          this.$message({
            showClose: true,
            message: 'Unbind successfully',
            type: 'info'
          });
        } else {
          // 执行绑定逻辑
          this.bindInstanceId(row);
          this.$message({
            showClose: true,
            message: 'Binding successful',
            type: 'success'
          });
        }
      }
    },
    bindInstanceId(row) {
      // 执行绑定逻辑
      // 将 row.id 添加到 boundInstances 中
      console.log(this.boundInstances,typeof(this.boundInstances));
      this.boundInstances.push(row.id);
    },
    unBindInstanceId(row) {
      // 执行解绑逻辑
      // 从 boundInstances 中移除 row.id
      const index = this.boundInstances.indexOf(row.id);
      if (index !== -1) {
        this.boundInstances.splice(index, 1);
      }
    },
    tableRowClassName ({ row, rowIndex }) {
      row.rowIndex = rowIndex;
    },
    // 下载按钮
    download(data){
      // console.log(data,'11s');
      // console.log(allData,'12s');
      if(data.url){
        let urls = data.url;
        // 创建一个链接元素
        const link = document.createElement('a');
        link.href = urls;
        link.target = "_blank"; // 在新窗口中打开链接
        link.download = 'downloaded_file'; // 设置下载的文件名
        // 模拟点击链接，触发下载
        link.click();
      }else{
        this.$message({
          showClose: true,
          message: 'Data lost',
          type: 'warning'
        });
      }

    },
    // 处理展开表的数据，让input、output和parameter依次输出
    dataTable(val){
      let newVal = [];
      for(let i = 0 ; i<val.inputs.length ; i++){
        let data = {};
        data.name = val.inputs[i].name;
        data.type = "inputData";
        data.description = val.inputs[i].description;
        data.url = val.inputs[i].value;
        newVal.push(data);
      }
      for(let i = 0 ; i<val.outputs.length ; i++){
        let data = {};
        data.name = val.outputs[i].name;
        data.type = "outputData";
        data.description = val.outputs[i].description;
        data.url = val.outputs[i].value;
        newVal.push(data);
      }
      for(let i = 0 ; i<val.parameters.length ; i++){
        console.log(val.parameters[i],i);
        let data = {};
        data.name = val.parameters[i].name;
        data.type = "parameters";
        data.description = val.parameters[i].description;
        data.url = val.parameters[i].value;
        newVal.push(data);
      }
      console.log(newVal,'new');
      return newVal;
    },
    // 过滤方法，没有udxNode变量就加一个，暂时不知道udxNode是干啥的
    filterUdxNode(event) {
      if (
        Object.prototype.hasOwnProperty.call(
          event.datasetItem,
          "UdxDeclaration"
        )
      ) {
        if (event.datasetItem.UdxDeclaration[0].UdxNode != "") {
          if (
            Object.prototype.hasOwnProperty.call(
              event.datasetItem.UdxDeclaration[0].UdxNode[0].UdxNode[0],
              "UdxNode"
            )
          ) {
            return false;
          } else {
            let udxNode = event.datasetItem.UdxDeclaration[0].UdxNode;
            return udxNode;
          }
        }
      }
    },
    // 判断当前 modelInEvent 是否被选中
    isDataSelected(modelInEvent) {
        // 使用 some 方法检查是否存在匹配的对象
      return this.selectedDataEvent.some(item => {
        return item.value === modelInEvent.value;
      });
    },
    // 选择数据后清除数据
    clearData(modelInEvent){
      // console.log(modelInEvent,'111');
      let index = this.selectedDataEvent.findIndex(item => {
        return item.value === modelInEvent.value;
      });
      if (index !== -1) {
        this.selectedDataEvent.splice(index, 1);
      }
      // 这两个属性是选择数据后新增的，起码要清空
      this.currentEvent = modelInEvent;
      this.currentEvent.value = null;
      this.currentEvent.dataId = null;
    },
    // 控制选择数据dialogue
    async selectDataDialog(event) {
      let project = await getProjectById(this.$router.currentRoute.params.id);
      this.scenario = await getScenarioById(project.scenario);
      this.modelInstanceList = await getInstancesInScenario(
        this.scenarioId,
        this.modelItem.id
      );
      await this.getInstanceStatus();
      this.currentEvent = event;
      this.selectDataDialogShow = true;
    },
    // 选择数据提交按钮的方法，数据只是缓存在浏览器中 1、提取并存储文件名 2、将数据的id和url存储起来
    submitDataToEvent(val) {
      // console.log(val,'val');
      // console.log(this.selectedDataEvent,'push前');
      let fileName = val.name+val.suffix;
      let data = {};
      data.fileName = fileName
      data.value = val.value
      if(data.value){
        this.selectedDataEvent.push(data);
      }else{
        this.$message({
          showClose: true,
          message: 'Data failure',
          type: 'warning'
        });
      }
      this.currentEvent.value = val.value;
      this.currentEvent.dataId = val.id;
      this.selectDataDialogShow = false;
    },
    getFileName(val){
      // 使用 find 方法查找匹配的元素
      let matchedElement = this.selectedDataEvent.find(item => {
        return item.value === val.value;
      });
      if (matchedElement) {
        // 如果找到匹配的元素，获取其 fileName 属性的值
        var fileName = matchedElement.fileName;
      }
      return fileName;
    },
    async getInstances() {
      let project = await getProjectById(this.$router.currentRoute.params.id);
      this.scenario = await getScenarioById(project.scenario);
      console.log(this.scenario,'scenario');
      if(Object.prototype.hasOwnProperty.call(this.scenario, 'instances')){
        this.boundInstances = this.scenario.instances;
      }
      this.instanceDialogShow = true;
      this.modelInstanceList = await getInstancesInScenario(
        this.scenarioId,
        this.modelItem.id
      );
      // console.log(this.modelInstanceList,'111');
      // console.log(this.modelInstanceList,'111');
      await this.getInstanceStatus();

    },
     getInstanceStatus() {
      this.modelInstanceList.forEach(async(instance) =>  {
        if (instance.status == 0) {
         await this.getOutputs(instance, instance.refreshForm);
          //表示正在运行
        }
      });
    },
    // selectData(val) {
    //   let stateIndex = this.stateList.findIndex(
    //     (state) => state.name == this.currentEvent.stateName
    //   );

    //   let eventIndex = this.stateList[stateIndex].Event.findIndex(
    //     (event) => event.name == this.currentEvent.name
    //   );
    //   this.$set(
    //     this.stateList[stateIndex].Event[eventIndex],
    //     "url",
    //     val.pathURL
    //   );
    //   this.$set(
    //     this.stateList[stateIndex].Event[eventIndex],
    //     "urlName",
    //     val.name
    //   );

    //   this.selectDataDialogShow = false;
    // },
    // 运行invoke
    async invokeTest() {
      // debugger;
      try{
        console.log("1号位置执行");
        await this.$confirm('Are you sure to invoke the model?', 'confirm', {
          confirmButtonText: 'Yes',
          cancelButtonText: 'No',
          type: 'warning'
        });
        console.log("说明不是elementui的问题");
        await this.createFilefromParam();
        console.log("2号位置执行");
        this.createInvokeForm();
        //测试数据没有弄 直接运行 根据ip+id
        //invoke
        console.log(this.invokeForm,"3号执行");
        let data = await invokeSingleModel(this.invokeForm);
        console.log(data,'invokeSingleModel返回结果');
        let refreshForm = {
          ip: this.invokeForm.ip,
          port: this.invokeForm.port,
        };
        console.log("4号执行");
        if (data == null) {
          this.$message({
            message: "You have run the model failed",
            type: "error",
          });
          console.log("5号执行");
          await this.emitInstance(-1, this.modelItem, refreshForm);
          console.log("6号执行");
        } else {
          console.log(data,"5号else执行");
          refreshForm.tid = data.data.tid;
          console.log("refreshForm", "refreshForm", refreshForm);
          await this.emitInstance(0, this.modelItem, refreshForm);
          console.log("6号else执行");
        }
      }catch{
        this.$message({
            type: 'info',
            message: 'invoke failed'
        });  
      }
    },
    // 创建运行时的instance
    async emitInstance(status, modelItem, refreshForm) {
      //在运行时 instance的创建
      if (status == 0 || status == -1) {
        let instanceTemp = {
          name: modelItem.name + "||" + dateFormat(new Date()),
          behavior: modelItem.behavior,
          status: status,
          scenarioId: this.scenarioId,
          modelId: modelItem.id,
          refreshForm: refreshForm,
          isReproduced: false,
        };
        await saveInstance(instanceTemp);
      } else {
        let instanceTemp = {
          behavior: modelItem.behavior,
          status: status,
        };
        console.log(instanceTemp);

        let data = await updateInstance(modelItem.id, instanceTemp);
        console.log("over", data);
      }
    },

    async getOutputs(instance, refreshForm) {
      // debugger;
      //获得结果
      if (instance.status != 2) {
        let { data } = await getRecordofSingleModel(refreshForm);
        // console.log(data);

        if (data.status != instance.status) {
          instance.status = data.status;
          if (data.status == 2) {
            await this.getStateEventOut(instance, data);
            // let data3 = await updateInstance(this.scenarioId, stepResource);
          }
        }
      }
    },

    async getStateEventOut(instance, record) {
      let stateList = instance.behavior;
      let outputUrl = record.outputs;
      outputUrl.forEach((el) => {
        stateList.forEach((state, index) => {
          if (state.name == el.statename) {
            state.outputs.forEach((event, eventIndex) => {
              if (el.event == event.name) {
                this.$set(
                  instance.behavior[index].outputs[eventIndex],
                  "value",
                  el.url
                );
              }
            });
          }
        });
      });

      console.log(instance);
      await this.emitInstance(instance.status, instance, instance.refreshForm);
    },
    // 创建一个invokeForm，清洗参数，将各数据放到invokeForm中
    createInvokeForm() {
      console.log(this.modelItem,'00');
      let stateList = this.modelItem.behavior;
      let input = [];
      let output = [];
      // debugger;
      for (let i = 0; i < stateList.length; i++) {
        let state = stateList[i];
        let allInputsWithPara = state.inputs.concat(state.parameters);
        let detail = {};
        for (let j = 0; j < allInputsWithPara.length; j++) {
          //判断数据类型 如果是input--对应url

          detail["statename"] = state.name;
          detail["event"] = allInputsWithPara[j].name;

          if (Object.hasOwnProperty.call(allInputsWithPara[j], "value")) {
            detail["tag"] = allInputsWithPara[j].name;
            detail["url"] = allInputsWithPara[j].value;
            input.push(detail);
          } else {
            continue;
          }
        }

        for (let j = 0; j < state.outputs.length; j++) {
          let template = {};
          let outputTemplate = state.outputs[j].datasetItem;
          if (outputTemplate.type === "external") {
            template = {
              type: "id",
              value: outputTemplate.externalId,
            };
          } else {
            template = {
              type: "none",
              value: "",
            };
          }
          detail["template"] = template;
          output.push(detail);
        }
      }

      this.invokeForm.inputs = input;
      this.invokeForm.outputs = output; 
      console.log(this.invokeForm,'000');
    },
    // 点击state使其收缩的事件，感觉没啥用啊
    handleChange() {
    },

    // 将参数绑定为一个xml文件，上传，返回url绑定到mdl
    async createFilefromParam() {
      // console.log("111", this.modelItem);
      let stateList = this.modelItem.behavior;
      console.log("file1");
      for (let i = 0; i < stateList.length; i++) {
        let events = stateList[i].parameters;
        console.log("file2");
        for (let j = 0; j < events.length; j++) {
          //判断如果是参数的话，重新绑定成为一个文件 之后上传 返回url绑定到mdl中去
          let content = "";
          // let uploadFileForm = new FormData();
          console.log("file3");
          let udxNodeList =
            events[j].datasetItem.UdxDeclaration[0].UdxNode[0].UdxNode;
            // events[j].datasetItem.UdxDeclaration[0].UdxNode;
          console.log("file4");
          for (let k = 0; k < udxNodeList.length; k++) {
            console.log(udxNodeList[k],'file5');
            if (Object.hasOwnProperty.call(udxNodeList[k], "value")) {
              console.log("file6");
              // content += `<XDO name="${udxNodeList[k].name}" kernelType="${udxNodeList[k].type}" value="${udxNodeList[k].value}" />`;
              content += `<XDO name="${udxNodeList[k].name}" kernelType="real" value="${udxNodeList[k].value}" />`;
            }
          }
          console.log(content,"file7");
          if (content != "") {
            content = "<Dataset> " + content + " </Dataset>";
            let file = new File([content], events[j].name + ".xml", {
              type: "text/plain",
            });
            console.log(content,'content-content-content');
            // uploadFileForm.append("file", file);

            // this.createConfigFile();
            console.log("file8");
            await this.submitUpload(i, j, file);
            console.log("file9");
          }
        }
      }
    },
    // 上传文件，并保留url
    async submitUpload(stateIndex, eventIndex, fileItem) {
      
      // let param = fileItem.file;
      let uploadFileForm = new FormData();
      uploadFileForm.append("file", fileItem);
      console.log("file8.2");
      let data = await saveData(uploadFileForm, renderSize(fileItem.size) ,"intermediate");
      console.log("file8.2",data);
      this.$set(
        this.modelItem.behavior[stateIndex].parameters[eventIndex],
        "value",
        // data.value
        data
      );

    },
    //在点击model的时候就初始化一个task，获取模型所在位置的ip，端口，md5值（模型的唯一标识符）
    async initTask() {
      //get task ip port ...
      console.log(this.modelItem,'1101');
      let {data}  = await initTask(this.modelItem.md5);
      console.log(data,'data');
      this.invokeForm.ip = data.host;
      this.invokeForm.port = data.port;
      this.invokeForm.pid = this.modelItem.md5; //md5
      this.invokeForm.username = this.userId;
    },
  },

  beforeDestroy() {
    clearInterval(this.timer);
  },

  async mounted() {
  },
};
</script>
<style lang="scss" scoped>
.selectData {
  /deep/.el-dialog {
    height: 800px;
    overflow: auto;
  }
}
</style>
