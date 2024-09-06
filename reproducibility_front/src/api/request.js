import { get, post, patch } from "@/axios";
import { successNotification } from "@/utils/notification";
import axios from "axios";

//-------------------------------------------------users--------------------------------------

export async function updateUserCreatedProjects(form) {
  let data = await patch(`/users`, form);
  // if (data != null) {
  //   successNotification('update', 'user');
  // }
  return data;
}

export async function saveUser(form) {
  let data = await post(`/users/register`, form);
  if (data != null) {
    successNotification("create", "user");
  }
  return data;
}

//根据project来获取 不要暴露user接口
export async function getUserInfoByUserId(userId) {
  return await get(`/users/getUserInfoByUserId/${userId}`);
}

export async function getUser() {
  return await get(`/users`);
}

export async function updateUsersModel(json) {
  return await patch(`/users/model`, json);
}

export async function getUserProjects() {
  return await get(`/users/getUserProjects`);
}

//----------------------------------------------------projects-----------------------------------------

//获取project的star数
// export async function getStarredCount(projectId) {
//   return await get(`/projects/getStarredCount/${projectId}`);
// }

export async function getProjectAndUsers(projectId) {
  return await get(`/projects/user/${projectId}`);
}

export async function getAllProjects(currentPage, pagesize) {
  let data = await get(`/projects/${currentPage}/${pagesize}`);
  return data;
}

export async function getProjectById(projectId) {
  return await get(`/projects/info/${projectId}`);
}

export async function getMyProjects() {
  return await get(`/projects/getmyprojects`);
}

//将传输的pdf文件送到后端处理
export async function pdfConvert(fileContent) {
  return await post(`/projects/pdf`, fileContent);
}

//---------------

export async function updateProject(projectId, form) {
  let data = await patch(`/projects/${projectId}`, form);
  return data;
}

export async function updateProjectMembers(projectId, form) {
  let data = await patch(`/projects/memberList/${projectId}`, form);
  if (data != null) {
    successNotification("update", "project");
  }
  return data;
}

export async function saveProject(form) {
  let data = await post(`/projects`, form);
  // if (data != null) {
  //   successNotification('create', 'project')
  // }
  return data;
}

export async function folkProject(projectId) {
  let data = await post(`/projects/folk/${projectId}`);
  // if (data != null) {
  //   successNotification('create', 'project')
  // }
  return data;
}

//------------------------------------------manager server------------------------------------
export async function runtask(formData) {
  return await post(`/managerServer/runtask`, formData);
}

export async function checkTaskStatus(tid) {
  return await get(`/managerServer/checkTaskStatus/${tid}`);
}

//-------------------------------------------modelitems--------------------------------------------------

// export async function getModelsByPrivacy(privacy, currentPage, pagesize, key) {
//   return await post(
//     `/models/getPublicModels/${privacy}/${currentPage}/${pagesize}`,
//     key
//   );
// }
export async function getModelsByPrivacy(form) {
  return await post(`/models/getPublicModels`, form);
}

export async function saveNewCodeModel(form) {
  return await post(`/models/newCodeModel`, form);
}

export async function getMyModels() {
  return await get(`/models/my`);
}

// export async function getAllPublicModels() {
//   return await get(`/models/allPublic`);
// }

// export async function getPublicModelListByIgnoreName(text) {
//   return await get(`/models/getPublicModelListByIgnoreName/${text}`);
// }

export async function getModelById(id) {
  return await get(`/models/getModelById/${id}`);
}

export async function getAssessmentMethod() {
  return await get(`/models/getAssessmentMethod`);
}

export async function addModelByMD5Local(form) {
  return await get(`/models/local`, form);
}

// export async function getMyModels(id) {
//   return await get(`/models/getModelById/${id}`);
// }

//---

// export async function getAllModelItems(currentPage, pagesize) {
//   return await get(`/modelItems/${currentPage}/${pagesize}`);
// }

// export async function getModelsByProjectId(projectId) {
//   return await get(`/modelItems/getModelsByProjectId/${projectId}`);
// }

//------------------------------------------scenario------------------------------------
export async function getScenarioById(id) {
  return await get(`/scenario/${id}`);
}

export async function getScenariosByProjectId(projectId) {
  return await get(`/scenario/project/${projectId}`);
}

export async function getScenariosByScenarioId(scenarioId) {
  return await get(`/scenario/r/${scenarioId}`);
}

// export async function getScenarioAllInfoById(scenarioId) {
//   return await get(`/scenario/${scenarioId}`);
// }

export async function saveScenario(postJson, initialScenatioId) {
  return await post(`/scenario/${initialScenatioId}`, postJson);
}

export async function bindScenario(id, postJson) {
  return await patch(`/scenario/instance/${id}`, postJson);
}

export async function updateScenarioByProjectId(projectId, postJson) {
  let data = await patch(`/scenario/${projectId}`, postJson);
  if (data != null) {
    successNotification("update", "scenario");
  }
  return data;
}

export async function updateresourceCollection(id, form) {
  let data = await patch(`/scenario/resources/${id}`, form);
  return data;
}

export async function updateContainerId(formData) {
  return await post(`/scenario/containerId`, formData);
}

//-----------------------------------------------folder---------------------------------------------

export async function addFolder(form) {
  return await post(`/folders`, form);
}

export async function getFolders() {
  return await get(`/folders`);
}

export async function getFoldersByScenarioId(scenarioId) {
  return await get(`/folders/${scenarioId}`);
}

export async function getFoldersByTagId(tagId) {
  return await get(`/folders/${tagId}`);
}

export async function getFolderIdByDataItemId(dataId) {
  return await get(`/folders/getFolderIdByDataItemId/${dataId}`);
}

//-----------------------------------------------data---------------------------------------------

export async function saveData(form, fileSize, storedFolderId) {
  return await post(`/data/uploadFileForm/${fileSize}/${storedFolderId}`, form);
}

export async function saveDocument(form, fileSize, storedFolderId) {
  return await post(
    `/data/saveAsNewDocument/${fileSize}/${storedFolderId}`,
    form
  );
}

export async function replaceDocument(form, fileSize, storedFolderId) {
  return await post(
    `/data/replaceDocument/${fileSize}/${storedFolderId}`,
    form
  );
}

export async function getDataItems(dataItemIds) {
  return await post(`/data/getDataItems`, dataItemIds);
}

export async function postFile(form) {
  return await axios.post("http://112.4.132.6:8083/data", form);
}

//-----------------------------------------------dataContainer---------------------------------------------

export async function postDataContainer(form) {
  // debugger;
  let data = await post(`/dataContainer/uploadSingle`, form);
  return data;
}

export async function getDataServiceInfo(form) {
  debugger;

  return await post(`/dataContainer/dataService/getData`, form);
}

export async function getDataServiceInfo1(form) {
  return await post(`/dataContainer/dataService/findData`, form);
}

export async function getAllProcessing(list) {
  return await get(`/dataContainer/dataService/getAllProcessing`, list);
}

//-----------------------------------------------manager server type 1---------------------------------------------
export async function initTask(md5) {
  return await get(`/managerServer/getServiceTask/${md5}`);
}

export async function createTask(obj) {
  return await post(`/managerServer/initTask/`, obj);
}

export async function invokeSingleModel(formData) {
  return await post(`/managerServer/invoke`, formData);
}

export async function getRecordofSingleModel(obj) {
  return await post(`/managerServer/refresh`, obj);
}

//-----------------------------------------------model instances---------------------------------------------
export async function getInstanceById(id) {
  return await get(`/model_instances/${id}`);
}

export async function getInstancesInScenario(
  scenarioId,
  modelId,
  isReproduced
) {
  return await get(
    `/model_instances/inscenario/${scenarioId}/${modelId}/${isReproduced}`
  );
}

export async function getInstanceByScenarioId(scenarioId) {
  return await get(`/model_instances/getByScenarioId/${scenarioId}`);
}

export async function getAssessmentInstancesInScenario(scenarioId) {
  return await get(`/model_instances/inscenario/assessment/${scenarioId}`);
}

export async function getInstancesByIds(instances) {
  return await post(`/model_instances/getBoundInstances`, instances);
}

export async function saveInstance(formData) {
  return await post(`/model_instances`, formData);
}

export async function updateInstance(id, formData) {
  return await patch(`/model_instances/${id}`, formData);
}

//===============================assessment=================================================

export async function getAssessment(id) {
  return await patch(`/assessment/${id}`);
}

export async function startAssessment(formData) {
  return await patch(`/assessment/start`, formData);
}

export async function autoAssessment(reproducedScenarioId) {
  return await patch(`/assessment/${reproducedScenarioId}`);
}

//===============================docker-codeOnline=================================================

//docker
export async function codingPython(code) {
  return await post(`/execute-python`, code);
}

//===============================envsAutoConfig=================================================

// getWorkDirectory
export async function getDirectory(formData) {
  return await post(`/envs/getWorkDirectory`, formData);
}

//------------------python38-back------------------

// 步骤1：生成dockerfile
// export async function getPython38DockerFile() {
//   return await get(`/envs/py38dockerFile`);
// }

// 步骤2：上传数据和代码文件
// export async function uploadDataAndCode(formData) {
//   return await post(`/envs/uploadDataAndCode`, formData);
// }

// // 步骤3（可选）：创建自动识别依赖库
// export async function createDependency() {
//   return await get(`/envs/createDependency`);
// }

// // 步骤4（可选）：编辑工作目录中文件
// export async function editUploadedFile(path) {
//   return await post(`/envs/editUploadedFile`, path);
// }

// // 步骤5（可选）：保存编辑好的文件
// export async function SaveFileContent(forData) {
//   return await post(`/envs/SaveFileContent`, forData);
// }

// 步骤6：构建镜像
// export async function buildImage() {
//   return await get(`/envs/buildImage`);
// }

// 步骤7：生成容器并执行代码
export async function runContainer(forData) {
  return await post(`/envs/runContainer`, forData);
}

// 将输出数据上传至数据容器，并更新folders、modelItem
export async function uploadAndUpdateOutput(formData) {
  return await post(`/envs/uploadAndUpdateOutput`, formData);
}

// 步骤8：销毁所有容器，删除镜像，清空工作目录
export async function overReproTask() {
  return await get(`/envs/deleteContainerAndImage`);
}

//------------------python38------------------

// 步骤1：（创建场景的时候）创建容器，并将容器的id写入对应场景
export async function createContainer(formData) {
  return await post(`/envs/createContainer`, formData);
}

// 步骤2：上传数据和代码文件
export async function uploadDataAndCode(formData) {
  return await post(`/envs/uploadDataAndCode`, formData);
}

// 步骤3（可选）：创建自动识别依赖库
export async function createDependency(formData) {
  return await post(`/envs/createDependency`, formData);
}

// 步骤4（可选）：编辑工作目录中文件
export async function editUploadedFile(formData) {
  return await post(`/envs/editUploadedFile`, formData);
}

// 步骤5（可选）：保存编辑好的文件
export async function SaveFileContent(formData) {
  return await post(`/envs/SaveFileContent`, formData);
}

// 步骤6：安装依赖
export async function installDependencies(formData) {
  return await post(`/envs/installRequires`, formData);
}

// 步骤7：反复利用该容器执行代码
export async function executeScript(formData) {
  return await post(`/envs/executeScript`, formData);
}
////===============================Ai Assistant=================================================

// 获取pdf解析结果
export async function getPdfContent(pdfFile) {
  return await post(`/AI/pdfParsing`, pdfFile);
}

export async function modelQuery(formData) {
  return await post(`/AI/modelQuery`, formData);
}

//===============================extra=================================================
