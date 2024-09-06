import {dateFormat} from "@/lib/utils";
import {renderSize} from "@/utils/utils";
import {
    saveData,
    saveInstance,
    updateInstance,
    invokeSingleModel,
} from "@/api/request";
import {errorNotification, successNotification} from "@/utils/notification";

export async function startInvokeModel(
    modelItem,
    invokeForm,
    modelInstanceName_input,
    invokingType,
    initialScenarioId,
    reproducedScenarioId
) {
    try {
        let modelItemNew = await createFilefromParam(modelItem);
        let invokeFormNew = await createInvokeForm(modelItemNew, invokeForm);
        console.log(modelItemNew, invokeFormNew, "invokeFormNew");
        let data = await invokeSingleModel(invokeFormNew);
        let refreshForm = {
            ip: invokeFormNew.ip,
            port: invokeFormNew.port,
        };

        if (data == null) {
            errorNotification("You have run the model failed");
            await emitInstance(
                -1,
                modelItemNew,
                refreshForm,
                modelInstanceName_input,
                invokingType,
                initialScenarioId,
                reproducedScenarioId
            );
        } else {
            refreshForm.tid = data.data.tid;
            await emitInstance(
                0,
                modelItemNew,
                refreshForm,
                modelInstanceName_input,
                invokingType,
                initialScenarioId,
                reproducedScenarioId
            );
        }
        successNotification("start", "calculation");
    } catch {
        errorNotification("You have run the model failed");
    }
}

export function getInstanceName(invokingType, name) {
    if (invokingType == "construction") {
        return name + "  ||  " + dateFormat(new Date());
    } else if (invokingType == "reproduction") {
        return "Reproduction ||  " + name + "  ||  " + dateFormat(new Date());
    } else {
        return "Assessment of ||  " + name + "  ||  " + dateFormat(new Date());
    }
}

export async function createFilefromParam(modelItem) {
    let stateList = modelItem.behavior;
    for (let i = 0; i < stateList.length; i++) {
        let events = stateList[i].parameters;
        if (events.length == 0) {
            return;
        }
        //不能用forEach
        for (let j = 0; j < events.length; j++) {
            console.log("1.1", events);
            let content = "";
            // let uploadFileForm = new FormData();
            //这一块应该是重新梳理过的模型结构，是新改的，但是跟以前的不适配，暂时注释掉
            let udxNodeList = events[j].datasetItem.UdxDeclarationNew;

            for (let k = 0; k < udxNodeList.length; k++) {
                if (Object.hasOwnProperty.call(udxNodeList[k], "parameterValue")) {
                    content += `<XDO name="${events[j].name}" kernelType="string" value="${udxNodeList[k].parameterValue}" />`;
                }
            }
            if (content != "") {
                content = "<Dataset> " + content + " </Dataset>";
                let file = new File([content], events[j].name + ".xml", {
                    type: "text/plain",
                });
                let dataValue = await submitUpload(file);

                modelItem.behavior[i].parameters[j]["value"] = dataValue;
            }
        }
    }
    return modelItem;
}

export async function submitUpload(fileItem) {
    let uploadFileForm = new FormData();
    uploadFileForm.append("file", fileItem);
    let data = await saveData(
        uploadFileForm,
        renderSize(fileItem.size),
        "intermediate"
    );
    return data;
}

export function createInvokeForm(modelItem, invokeForm) {
    let stateList = modelItem.behavior;
    invokeForm.inputs = [];
    invokeForm.outputs = [];

    // debugger;
    for (let i = 0; i < stateList.length; i++) {
        let state = stateList[i];
        let allInputsWithPara = state.inputs.concat(state.parameters);
        allInputsWithPara.forEach((item) => {
            if (
                Object.hasOwnProperty.call(item, "value") &&
                item.value != "" &&
                item.value != null
            ) {
                let detail = {
                    statename: state.name,
                    event: item.name,
                    tag: item.name,
                    url: item.value,
                };

                invokeForm.inputs.push(detail);
            }
        });
        state.outputs.forEach((item) => {
            let template = {};

            let outputTemplate = item.datasetItem;
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
            let detail = {
                statename: state.name,
                event: item.name,
                template: template,
            };

            invokeForm.outputs.push(detail);
        });
    }

    return invokeForm;
}

export async function emitInstance(
    status,
    modelItem,
    refreshForm,
    invokingType,
    modelInstanceName_input,
    initialScenarioId,
    reproducedScenarioId
) {
    //在运行时 instance的创建
    if (status == 0 || status == -1) {
        let instanceTemp = {
            name: modelInstanceName_input,
            modelName: modelItem.name,
            behavior: modelItem.behavior,
            status: status,
            scenarioId: judgeIsReproducedScenarioId(
                invokingType,
                initialScenarioId,
                reproducedScenarioId
            ),
            modelId: modelItem.id,
            refreshForm: refreshForm,
            isReproduced: invokingType == "reproduction" ? true : false,
        };

        await saveInstance(instanceTemp);
    } else {
        await updateInstance(status, modelItem.behavior, modelItem.id);
    }
}

export function judgeIsReproducedScenarioId(
    invokingType,
    initialScenarioId,
    reproducedScenarioId
) {
    if (
        initialScenarioId == "" ||
        initialScenarioId == undefined ||
        initialScenarioId == null
    ) {
        return reproducedScenarioId;
    }
    if (invokingType == "construction") {
        return initialScenarioId;
    } else {
        return reproducedScenarioId;
    }
}

export async function updateInstances(status, behavior, instanceId) {
    await updateInstance(instanceId, {
        behavior: behavior,
        status: status,
    });
}

export function downloadData(data) {
    if (data.value) {
        let urls = data.value;
        // 创建一个链接元素
        const link = document.createElement("a");
        link.href = urls;
        link.target = "_blank"; // 在新窗口中打开链接
        link.download = data.datasetItem.dataName; // 设置下载的文件名
        // 模拟点击链接，触发下载
        link.click();
    } else {
        errorNotification("Data lost");
    }
}

// export function filterStates(state) {
//   let array = [];
//   this.expandRows = [];
//   state.inputs.forEach((item) => {
//     item.stateName = state.name;
//     item.type = "inputs";
//     array.push(item);
//   });
//   state.parameters.forEach((item) => {
//     item.stateName = state.name;
//     item.type = "parameters";
//     // debugger;
//     array.push(item);
//     this.expandRows.push(item.eventId);
//   });
//   state.outputs.forEach((item) => {
//     item.stateName = state.name;
//     item.type = "outputs";
//     array.push(item);
//   });

//   return array;
// }
