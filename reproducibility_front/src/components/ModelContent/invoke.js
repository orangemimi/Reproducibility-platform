import {errorNotification} from "@/utils/notification";

import {
    saveData,
    invokeSingleModel,
    initTask,
    saveInstance,
    updateInstance,
    getInstanceById,
    getRecordofSingleModel,
    getInstancesInScenario,
    bindScenario,
    getScenarioById,
    getFolders,
    getFoldersByTagId,
} from "@/api/request";

export async function initModelTask(md5, userId) {
    let {data} = await initTask(md5);
    let canInvoke = false;
    let invokeForm = {};

    if (data == undefined || data == "") {
        canInvoke = false;
        errorNotification("This model cannot be executed now");
    } else {
        canInvoke = true;
        invokeForm.ip = data.host;
        invokeForm.port = data.port;
        invokeForm.pid = md5; //md5
        invokeForm.username = userId;
    }
    return {
        canInvoke,
        invokeForm,
    };
}
