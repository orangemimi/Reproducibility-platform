/**
 * 数据容器的上传搞得我身心俱疲
 * 直接写一个综合的数据容器交互方法库
 * 在这里添加和修改数据容器交互方法
 */
import { postDataContainer, saveData } from "@/api/request.js";
import { renderSize } from "@/utils/utils";

// 接收一个multiFiles(还没做多文件的遍历存储)，并上传到数据容器，返回文件名和url后缀
export const uploadDataContainer = async (multiFiles) => {
  let uploadFormData = new FormData();
  uploadFormData.append("file", multiFiles);
  let dataUrl = await postDataContainer(uploadFormData);
  return dataUrl;
};

// 接收一个multiFiles(还没做多文件的遍历存储)，并上传到数据容器，更新数据库（folder、dataItem等）返回文件名和url后缀
// export const uploadDataContainer = async (multiFiles) => {
//   let uploadFormData = new FormData();
//   uploadFormData.append("file", multiFiles);
//   let dataUrl = await postDataContainer(uploadFormData);
//   return dataUrl;
// };

// 上传file，上传到数据容器并更新数据库（dataItem和folder)
// 接收两个参数：单个文件对象file，要上传到folder的id
export const saveDataAndUpdate = async (file, folderId) => {
  let uploadFileForm = new FormData();
  uploadFileForm.append("file", file, file.name);

  let folderInfo = await saveData(
    uploadFileForm,
    renderSize(file.size),
    folderId
  );
  return folderInfo;
};
