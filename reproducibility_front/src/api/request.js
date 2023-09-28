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
  if (data != null) {
    successNotification("create", "project");
  }
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
  return await post(
    `/models/getPublicModels`,
    form
  );
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

export async function saveScenario(postJson) {
  return await post(`/scenario`, postJson);
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

//-----------------------------------------------folder---------------------------------------------

export async function addFolder(form) {
  return await post(`/folders`, form);
}

export async function getFolders() {
  return await get(`/folders`);
}

//-----------------------------------------------data---------------------------------------------

export async function saveData(form, fileSize, storedFolderId) {
  return await post(`/data/uploadFileForm/${fileSize}/${storedFolderId}`, form);
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

export async function getInstancesInScenario(scenarioId, modelId) {
  return await get(`/model_instances/inscenario/${scenarioId}/${modelId}`);
}

//这是一个另类，读取数据但是用的POST，注意
export async function getInstancesByIds(instances) {
  return await post(`/model_instances/getBoundInstances`, instances);
}

export async function saveInstance(formData) {
  return await post(`/model_instances`, formData);
}

export async function updateInstance(id, formData) {
  return await patch(`/model_instances/${id}`, formData);
}

//===============================extra=================================================
export async function postFile(form) {
  return await axios.post("http://221.226.60.2:8082/data", form);
}
