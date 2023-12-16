package edu.njnu.opengms.r2.domain.scenario;


import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
<<<<<<< Updated upstream
import edu.njnu.opengms.r2.domain.model.ModelRepository;
import edu.njnu.opengms.r2.domain.project.ResourceCollection;
=======
import edu.njnu.opengms.r2.domain.dataItem.DataItem;
import edu.njnu.opengms.r2.domain.dataItem.DataItemRepository;
import edu.njnu.opengms.r2.domain.folder.Folder;
import edu.njnu.opengms.r2.domain.folder.FolderRepository;
import edu.njnu.opengms.r2.domain.folder.FolderService;
import edu.njnu.opengms.r2.domain.folder.dto.AddFolderDTO;
import edu.njnu.opengms.r2.domain.folder.dto.UpdateFolderChildrenDTO;
import edu.njnu.opengms.r2.domain.model.ModelRepository;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstanceController;
>>>>>>> Stashed changes
import edu.njnu.opengms.r2.domain.scenario.dto.AddScenarioDTO;
import edu.njnu.opengms.r2.domain.scenario.dto.UpdateScenarioDTO;
import edu.njnu.opengms.r2.domain.scenario.dto.UpdateScenarioInstanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @Author ：Zhiyi
 * @Date ：2021/4/1 22:42
 * @modified By：
 * @version: 1.0.0
 */
@Service
public class ScenarioService {
    @Autowired
    ScenarioRepository scenarioRepository;

    @Autowired
    ModelRepository modelRepository;

<<<<<<< Updated upstream
=======
    @Autowired
    DataItemRepository dataItemRepository;

    @Autowired
    ModelInstanceController modelInstanceController;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    FolderService folderService;


>>>>>>> Stashed changes
    public JSONObject getScenario(String id) {

        JSONObject obj = new JSONObject();
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
        JSONObject modelList =  Optional.ofNullable(scenario)
                .map(x -> x.getResourceCollection())
                .map(x -> {
                    List<String> modelIdList = x.getModelList();

                    if (modelIdList != null) {
                        obj.put("modelList", modelRepository.findAllById(modelIdList));
                    } else {
                        obj.put("modelList", null);
                    }
<<<<<<< Updated upstream
                    return obj;
                })
                .orElseGet(null);
=======
                    List<String> dataIdList = x.getDataList();
                    if (dataIdList != null) {
                        List dataListWithObject = new ArrayList();
                        for(String dataid : dataIdList){
                            DataItem dataItem = dataItemRepository.findById(dataid).orElseThrow(MyException::noObject);
                            dataListWithObject.add(dataItem);

                        }
                        obj.put("dataList", dataListWithObject);

                    } else {
                        obj.put("dataList", null);
                    }
                    return obj;
                })
                .orElseGet( () -> {
//                    JSONObject defaultObject = new JSONObject();
//                    defaultObject.put("instanceObjectList", null);
                    return null;
                });

        JSONObject instanceList =  Optional.ofNullable(scenario)
                .map(x -> x.getInstances())
                .map(x -> {
                    List<String> instanceIdList = x;
                    if (instanceIdList != null) {
//                        scenarioNew.put("instanceObjectList", modelInstanceRepository.findAllById(instanceIdList));
//                        getAllInstances(modelInstanceIds)
                        scenarioNew.put("instanceObjectList", modelInstanceController.getAllInstances(instanceIdList));

                    } else {
                        scenarioNew.put("instanceObjectList", null);
                    }
                    return scenarioNew;
                })
                .orElseGet(() -> {
                    JSONObject defaultObject = new JSONObject();
                    defaultObject.put("instanceObjectList", null);
                    return defaultObject;
                });

>>>>>>> Stashed changes

        JSONObject scenarioNew = new JSONObject();
        scenarioNew.put("name", scenario.name);
        scenarioNew.put("id", scenario.id);
        scenarioNew.put("type", scenario.type);
        scenarioNew.put("resourceCollection", obj);


        return scenarioNew;
    }

    public List<JSONObject> getScenariosByProjectId(String projectId) {
        List<Scenario> scenarios = scenarioRepository.findAllByProjectId(projectId);
        List<JSONObject> jsonObjectList = new ArrayList<>();
        for(Scenario s : scenarios){
            jsonObjectList.add(getScenario(s.getId()));

        }
        return jsonObjectList;
    }

    public Scenario updateScenario(String id, UpdateScenarioDTO update) {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
        update.updateTo(scenario);
        return scenarioRepository.save(scenario);
    }

    public Scenario saveScenario(AddScenarioDTO add, String userId) {
        Scenario scenario = new Scenario();
//        scenario.setUserId(userId);
        add.convertTo(scenario);
        return scenarioRepository.insert(scenario);
    }


    public Scenario updateresourceCollection(String id, String type, String userId, List<String> modelList) {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
        ResourceCollection resourceCollection = null;
        if (type.equals("modelList")) {
            resourceCollection = ResourceCollection.builder().modelList(modelList).build();
            if (scenario.resourceCollection != null) {
                if (scenario.resourceCollection.getDataList() != null) {
                    resourceCollection.setDataList(scenario.resourceCollection.getDataList());
                }

            }
        }
        scenario.setResourceCollection(resourceCollection);

        return scenarioRepository.save(scenario);
    }

    public Scenario updateScenarioInstance(String id, UpdateScenarioInstanceDTO update) {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
        update.updateTo(scenario);
        return scenarioRepository.save(scenario);
    }
}
