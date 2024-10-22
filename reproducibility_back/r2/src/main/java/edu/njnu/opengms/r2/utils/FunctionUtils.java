package edu.njnu.opengms.r2.utils;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.dataItem.DataItem;
import edu.njnu.opengms.r2.domain.dataItem.DataItemRepository;
import edu.njnu.opengms.r2.domain.folder.FolderRepository;
import edu.njnu.opengms.r2.domain.folder.FolderService;
import edu.njnu.opengms.r2.domain.model.Model;
import edu.njnu.opengms.r2.domain.model.ModelRepository;
import edu.njnu.opengms.r2.domain.model.support.ModelEvent;
import edu.njnu.opengms.r2.domain.model.support.State;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstance;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstanceController;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstanceRepository;
import edu.njnu.opengms.r2.domain.scenario.ResourceCollection;
import edu.njnu.opengms.r2.domain.scenario.Scenario;
import edu.njnu.opengms.r2.domain.scenario.ScenarioRepository;
import edu.njnu.opengms.r2.domain.user.UserService;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2024/1/5 17:25
 * @modified By：
 * @version: 1.0.0
 */
@Service
public class FunctionUtils {
    @Autowired
    ModelInstanceRepository modelInstanceRepository;

    @Autowired
    ModelRepository modelRepository;

    @Autowired
    UserService userService;

    @Autowired
    ScenarioRepository scenarioRepository;


    @Autowired
    DataItemRepository dataItemRepository;

    @Autowired
    ModelInstanceController modelInstanceController;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    FolderService folderService;

    public List<JSONObject> getAllInstances(List<String> modelInstanceIds) {
        List<JSONObject> modelInstanceList = new ArrayList<>();
        for (String id : modelInstanceIds) {
            ModelInstance modelInstance = modelInstanceRepository.findById(id).orElseThrow(MyException::noObject);
            if (modelInstance != null) {
                modelInstanceList.add(getFullModelInstance(modelInstance));
            }
        }
        return modelInstanceList;
    }

    public JSONObject getFullModelInstance(ModelInstance modelInstance) {

        JSONObject object = new JSONObject();

        Model model = modelInstance.getModelId().equals("Currently unavailable")
                ? null
                : modelRepository.findById(modelInstance.getModelId()).orElseThrow(MyException::noObject);

        List<State> stateList = modelInstance.getBehavior();
        for (State state : stateList) {
            List<ModelEvent> inputEvents = state.getInputs();
            List<ModelEvent> outputEvents = state.getOutputs();
            for (ModelEvent input : inputEvents) {
                DataItem dataItem = dataItemRepository.findById(input.getDataId()).orElseThrow(MyException::noObject);
//                JSONObject obj = new JSONObject();
                JSONObject datasetItem = input.getDatasetItem();
//                datasetItem.put("value",dataItem.getValue());
                datasetItem.put("dataName", dataItem.getName() + "." + dataItem.getSuffix());
                datasetItem.put("isInitial", dataItem.getIsInitial());
                datasetItem.put("isReproduced", dataItem.getIsReproduced());
                datasetItem.put("isParameter", dataItem.getIsParameter());
                datasetItem.put("isIntermediate", dataItem.getIsIntermediate());


            }
            for (ModelEvent output : outputEvents) {
                JSONObject datasetItem = output.getDatasetItem();
                if (output.getDataId() != null) {
                    DataItem dataItem = dataItemRepository.findById(output.getDataId()).orElseThrow(MyException::noObject);
//                    datasetItem.put("value",dataItem.getValue());
                    datasetItem.put("dataName", dataItem.getName() + "." + dataItem.getSuffix());
                    datasetItem.put("isInitial", dataItem.getIsInitial());
                    datasetItem.put("isReproduced", dataItem.getIsReproduced());
                    datasetItem.put("isIntermediate", dataItem.getIsIntermediate());
                }


            }

        }


        object.put("modelDescription", (model != null) ? model.getDescription() : null);
        object.put("name", modelInstance.getName());
        object.put("id", modelInstance.getId());
        object.put("modelName", modelInstance.getModelName());
        object.put("modelId", modelInstance.getModelId());
        object.put("behavior", stateList);
        object.put("status", modelInstance.getStatus());
        object.put("executorId", modelInstance.getExecutorId());
        object.put("scenarioId", modelInstance.getScenarioId());
        object.put("refreshForm", modelInstance.getRefreshForm());
        object.put("isReproduced", modelInstance.getIsReproduced());
        object.put("createTime", modelInstance.getCreateTime());
        object.put("updateTime", modelInstance.getUpdateTime());

        JSONObject user = userService.getUserInfoById(modelInstance.getExecutorId());
        object.put("executorName", user.get("name"));
        return object;


    }

    public JSONObject getScenario(Scenario scenario) {

        JSONObject obj = new JSONObject();
        JSONObject scenarioNew = new JSONObject();
        ResourceCollection resourceCollection = scenario.getResourceCollection();

        ////get model list
//        List<String> modelIdList = resourceCollection.getModelList();
        if (resourceCollection != null) {
            if (resourceCollection.getModelList() != null) {
                obj.put("modelList", modelRepository.findAllById(resourceCollection.getModelList()));
            } else {
                obj.put("modelList", null);
            }

            //get datalist
//        List<String> dataIdList = resourceCollection.getDataList();
            if (resourceCollection.getDataList() != null) {
                List dataListWithObject = new ArrayList();
                for (String dataid : resourceCollection.getDataList()) {
                    DataItem dataItem = dataItemRepository.findById(dataid).orElseThrow(MyException::noObject);
                    dataListWithObject.add(dataItem);

                }
                obj.put("dataList", dataListWithObject);

            } else {
                obj.put("dataList", null);
            }
        }
        //get instances
//        List<String> instanceIdList = scenario.getInstances();
        if (scenario.getInstances() != null) {
            scenarioNew.put("instanceObjectList", getAllInstances(scenario.getInstances()));
        } else {
            scenarioNew.put("instanceObjectList", null);
        }

        //get initial scenario
//        String scenarioId = scenario.getInitialScenarioId();
        if (scenario.getInitialScenarioId() != null) {
            scenarioNew.put("initialScenarioObject", scenarioRepository.findById(scenario.getInitialScenarioId()));
        } else {
            scenarioNew.put("initialScenarioObject", null);
        }


        scenarioNew.put("name", scenario.getName());
        scenarioNew.put("id", scenario.getId());
        scenarioNew.put("env", scenario.getEnv());
        scenarioNew.put("containerId", scenario.getContainerId());
        scenarioNew.put("type", scenario.getType());
        scenarioNew.put("instances", scenario.getInstances());
        scenarioNew.put("resourceCollection", scenario.getResourceCollection());
        scenarioNew.put("resourceCollectionObjects", obj);
        scenarioNew.put("initialScenarioId", scenario.getInitialScenarioId());
        scenarioNew.put("flowData", scenario.getFlowData());


        return scenarioNew;


    }

    public static int downloadFileFromUrl(String url, String localFilePath) {

        try {
            URL httpUrl = new URL(url);
            File f = new File(localFilePath);
            FileUtils.copyURLToFile(httpUrl, f);

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    public static String readFile(String path) {
        String line = "";
        String pathname = path; // 绝对路径或相对路径都可以，写入文件时演示相对路径,读取以上路径的input.txt文件
        //防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw;
        //不关闭文件会导致资源的泄露，读写文件都同理
        //Java7的try-with-resources可以优雅关闭文件，异常时自动关闭文件；详细解读https://stackoverflow.com/a/12665271
        try (FileReader reader = new FileReader(pathname);
             BufferedReader br = new BufferedReader(reader) // 建立一个对象，它把文件内容转成计算机能读懂的语言
        ) {

            //网友推荐更加简洁的写法
            while ((line = br.readLine()) != null) {
                // 一次读入一行数据
                return line;
            }
        } catch (IOException e) {
            e.printStackTrace();

        }
        return line;
    }

//    public JSONObject upload2DataContainer(){
//
//    }


}
