package edu.njnu.opengms.r2.domain.scenario;


import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.dataItem.DataItemRepository;
import edu.njnu.opengms.r2.domain.folder.Folder;
import edu.njnu.opengms.r2.domain.folder.FolderRepository;
import edu.njnu.opengms.r2.domain.folder.FolderService;
import edu.njnu.opengms.r2.domain.folder.dto.AddFolderDTO;
import edu.njnu.opengms.r2.domain.folder.dto.UpdateFolderChildrenDTO;
import edu.njnu.opengms.r2.domain.model.ModelRepository;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstanceRepository;
import edu.njnu.opengms.r2.domain.scenario.dto.AddScenarioDTO;
import edu.njnu.opengms.r2.domain.scenario.dto.UpdateScenarioDTO;
import edu.njnu.opengms.r2.domain.scenario.dto.UpdateScenarioInstanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    DataItemRepository dataItemRepository;

    @Autowired
    ModelInstanceRepository modelInstanceRepository;

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    FolderService folderService;


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
                    List<String> dataIdList = x.getDataList();
                    if (modelIdList != null) {
                        obj.put("dataList", dataItemRepository.findAllById(dataIdList));
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
        scenarioNew.put("resourceCollection", scenario.resourceCollection);
        scenarioNew.put("resourceCollectionObjects", obj);
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

        Folder parentFolder = folderRepository.findByCreatorIdAndParent(userId, "0");

        //create folderScenario
        AddFolderDTO addScenarioFolderDTO = AddFolderDTO.builder()
                .level(1)
                .tagId(newScenario.getId())
                .name(newScenario.getName()+" --folder")
                .parent(parentFolder.getId())
                .build();

        Folder newScenarioFolder =  folderService.create(addScenarioFolderDTO,userId);


//        Folder childFolder = folderRepository.insert(folder);
        List<String> parentFolderChildren = new ArrayList<String>();
        String id = newScenarioFolder.getId();
        parentFolderChildren.add(id) ;

        UpdateFolderChildrenDTO updateProjectFolderChildrenDTO = UpdateFolderChildrenDTO.builder()
                .children(parentFolderChildren)
                .build();


        folderService.updateFolderChildren(parentFolder.getId(),updateProjectFolderChildrenDTO,userId);

        //todo


        return newScenario;
    }


//    public Scenario updateresourceCollection(String id,  String userId, UpdateResourceScenarioDTO update) {
//
//        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
//        update.convertTo(scenario);
//        ResourceCollection resourceCollection = null;
//        if (type.equals("modelList")) {
//            resourceCollection = ResourceCollection.builder().modelList(modelList).build();
//            if (scenario.resourceCollection != null) {
//                if (scenario.resourceCollection.getDataList() != null) {
//                    resourceCollection.setDataList(scenario.resourceCollection.getDataList());
//                }
//
//            }
//        }
//        if (type.equals("dataList")) {
//            resourceCollection = ResourceCollection.builder().dataList(modelList).build();
//            if (scenario.resourceCollection != null) {
//                if (scenario.resourceCollection.getDataList() != null) {
//                    resourceCollection.setModelList(scenario.resourceCollection.getDataList());
//                }
//
//            }
//        }
//        scenario.setResourceCollection(resourceCollection);
//
//        return scenarioRepository.save(scenario);
//    }

    public Scenario updateresourceCollection(String id,  String userId, JSONObject update) {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
        ResourceCollection resourceCollection =  ResourceCollection.builder()
                .modelList((List<String>) update.get("modelList"))
                .dataList((List<String>) update.get("dataList")).build();
        scenario.setResourceCollection(resourceCollection);
//        update.updateTo(scenario);
        return scenarioRepository.save(scenario);
    }

    public Scenario updateScenarioInstance(String id, UpdateScenarioInstanceDTO update) {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
        update.updateTo(scenario);
        return scenarioRepository.save(scenario);
    }
}
