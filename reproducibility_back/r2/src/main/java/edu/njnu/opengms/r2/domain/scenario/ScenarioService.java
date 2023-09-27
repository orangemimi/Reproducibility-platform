package edu.njnu.opengms.r2.domain.scenario;


import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.folder.Folder;
import edu.njnu.opengms.r2.domain.folder.FolderRepository;
import edu.njnu.opengms.r2.domain.model.ModelRepository;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstanceRepository;
import edu.njnu.opengms.r2.domain.project.ResourceCollection;
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

    @Autowired
    ModelInstanceRepository modelInstanceRepository;

    @Autowired
    FolderRepository folderRepository;

    public JSONObject getScenario(String id) {

        JSONObject obj = new JSONObject();
//        JSONObject objInstance = new JSONObject();
        JSONObject scenarioNew = new JSONObject();

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
                        scenarioNew.put("instanceObjectList", modelInstanceRepository.findAllById(instanceIdList));
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


        scenarioNew.put("name", scenario.name);
        scenarioNew.put("id", scenario.id);
        scenarioNew.put("type", scenario.type);
        scenarioNew.put("instances", scenario.instances);
        scenarioNew.put("resourceCollection", obj);
//        scenarioNew.put("instanceObjects", objInstance);


        return scenarioNew;
    }

    public List<Scenario> getScenariosByProjectId(String projectId) {
        List<Scenario> scenarios = scenarioRepository.findAllByProjectId(projectId);
        return scenarios;
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

        Scenario newScenario =  scenarioRepository.insert(scenario);

        Folder folder = new Folder();
        Folder parentFolder = folderRepository.findByCreatorIdAndParent(userId, "0");
        folder.setParent(parentFolder.getId());
        folder.setLevel("1");
        folder.setName(newScenario.getName());
        folder.setScenarioId(newScenario.getId());
        folderRepository.insert(folder);
        return newScenario;
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
