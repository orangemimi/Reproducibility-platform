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

export async function getUserInfoByUserId(userId) {
  return await get(`/users/getUserInfoByUserId/${userId}`);
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

//------------------------------------------manager server------------------------------------
export async function runtask(formData) {
  return await post(`/managerServer/runtask`, formData);
}

export async function checkTaskStatus(tid) {
  return await get(`/managerServer/checkTaskStatus/${tid}`);
}

//-------------------------------------------modelitems--------------------------------------------------

export async function getModelsByPrivacy(privacy, currentPage, pagesize) {
  return await get(
    `/models/getPublicModels/${privacy}/${currentPage}/${pagesize}`
  );
}

export async function getAllPublicModels() {
  return await get(`/models/allPublic`);
}

export async function getPublicModelListByIgnoreName(text) {
  return await get(`/models/getPublicModelListByIgnoreName/${text}`);
}

//---

export async function getAllModelItems(currentPage, pagesize) {
  return await get(`/modelItems/${currentPage}/${pagesize}`);
}

export async function getModelsByProjectId(projectId) {
  return await get(`/modelItems/getModelsByProjectId/${projectId}`);
}

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

export async function updateScenarioByProjectId(projectId, postJson) {
  let data = await patch(`/scenario/${projectId}`, postJson);
  if (data != null) {
    successNotification("update", "scenario");
  }
  return data;
}

export async function updateresourceCollection(id, type, form) {
  let data = await patch(`/scenario/resources/${id}/${type}`, form);

  return data;
}

//===============================extra=================================================
export async function postFile(form) {
  return await axios.post("http://221.226.60.2:8082/data", form);
}
