package edu.njnu.opengms.r2.domain.scenario;


import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.folder.Folder;
import edu.njnu.opengms.r2.domain.folder.FolderRepository;
import edu.njnu.opengms.r2.domain.folder.FolderService;
import edu.njnu.opengms.r2.domain.folder.dto.AddFolderDTO;
import edu.njnu.opengms.r2.domain.folder.dto.UpdateFolderChildrenDTO;
import edu.njnu.opengms.r2.domain.scenario.dto.*;
import edu.njnu.opengms.r2.utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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
    FolderRepository folderRepository;

    @Autowired
    FolderService folderService;

    @Autowired
    FunctionUtils functionUtils;


    public JSONObject getScenario(String id) {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
        return functionUtils.getScenario(scenario);
    }

//        JSONObject obj = new JSONObject();
//        JSONObject scenarioNew = new JSONObject();

//        Optional.ofNullable(scenario)
//                .map(x -> x.getResourceCollection())
//                .map(x -> {
//                    List<String> modelIdList = x.getModelList();
//
//                    if (modelIdList != null) {
//                        obj.put("modelList", modelRepository.findAllById(modelIdList));
//                    } else {
//                        obj.put("modelList", null);
//                    }
//                    List<String> dataIdList = x.getDataList();
//                    if (dataIdList != null) {
//                        List dataListWithObject = new ArrayList();
//                        for(String dataid : dataIdList){
//                            DataItem dataItem = dataItemRepository.findById(dataid).orElseThrow(MyException::noObject);
//                            dataListWithObject.add(dataItem);
//
//                        }
//                        obj.put("dataList", dataListWithObject);
//
//                    } else {
//                        obj.put("dataList", null);
//                    }
//                    return obj;
//                })
//                .orElseGet( () -> {
////                    JSONObject defaultObject = new JSONObject();
////                    defaultObject.put("instanceObjectList", null);
//                    return null;
//                });
//
//        Optional.ofNullable(scenario)
//                .map(x -> x.getInstances())
//                .map(x -> {
//                    List<String> instanceIdList = x;
//                    if (instanceIdList != null) {
////                        scenarioNew.put("instanceObjectList", modelInstanceRepository.findAllById(instanceIdList));
////                        getAllInstances(modelInstanceIds)
//                        scenarioNew.put("instanceObjectList", modelInstanceController.getAllInstances(instanceIdList));
//
//                    } else {
//                        scenarioNew.put("instanceObjectList", null);
//                    }
//                    return scenarioNew;
//                })
//                .orElseGet(() -> {
//                    JSONObject defaultObject = new JSONObject();
//                    defaultObject.put("instanceObjectList", null);
//                    return defaultObject;
//                });
//        Optional.ofNullable(scenario)
//                .map(x -> x.getInitialScenarioId())
//                .map(x -> {
//                    String scenarioId = x;
//                    scenarioNew.put("initialScenarioObject", scenarioRepository.findById(scenarioId));
//                    return scenarioNew;
//                })
//                .orElseGet(() -> {
//                    JSONObject defaultObject = new JSONObject();
//                    defaultObject.put("initialScenarioObject", null);
//                    return defaultObject;
//                });
//
//
//        scenarioNew.put("name", scenario.name);
//        scenarioNew.put("id", scenario.id);
//        scenarioNew.put("type", scenario.type);
//        scenarioNew.put("instances", scenario.instances);
//        scenarioNew.put("resourceCollection", scenario.resourceCollection);
//        scenarioNew.put("resourceCollectionObjects", obj);
//        scenarioNew.put("initialScenarioId", scenario.initialScenarioId);

//        return scenarioNew;
//    }

    public JSONObject getScenarioByInitialScenarioId(String scenarioId) {
        Scenario scenario = scenarioRepository.findByInitialScenarioId(scenarioId);
        if (scenario == null) {
            return null;
        } else {
            return functionUtils.getScenario(scenario);
        }
    }

    public List<JSONObject> getScenariosByProjectId(String projectId) {
        try{
            List<Scenario> scenarios = scenarioRepository.findAllByProjectId(projectId);
            List<JSONObject> jsonObjectList = new ArrayList<>();
            for (Scenario s : scenarios) {
                jsonObjectList.add(getScenario(s.getId()));

            }
            return jsonObjectList;
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Scenario updateScenario(String id, UpdateScenarioDTO update) {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
        update.updateTo(scenario);
        return scenarioRepository.save(scenario);
    }

    public Scenario updateScenarioForked(String id, UpdateForkedScenarioDTO updateForkedScenarioDTO) {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
        updateForkedScenarioDTO.updateTo(scenario);
        return scenarioRepository.save(scenario);
    }

    public Scenario saveScenario(AddScenarioDTO add, String userId, String initialScenatioId) {
        Scenario scenario = new Scenario();
        add.convertTo(scenario);
        scenario.setCreator(userId);
        if (!initialScenatioId.equals("initial")) {
            scenario.setInitialScenarioId(initialScenatioId);
        }

        Scenario newScenario = scenarioRepository.insert(scenario);

//        Folder parentFolder = folderRepository.findByCreatorIdAndParent(userId, "0");
//        //create folderScenario
//        AddFolderDTO addScenarioFolderDTO = AddFolderDTO.builder()
//                .level(1)
//                .tagId(newScenario.getId())
//                .name(newScenario.getName() + " --folder")
//                .parent(parentFolder.getId())
//                .build();
//        Folder newScenarioFolder = folderService.create(addScenarioFolderDTO, userId);


//        List<String> parentFolderChildren = new ArrayList<String>();
//        String id = newScenarioFolder.getId();
//        parentFolderChildren.add(id);

//        UpdateFolderChildrenDTO updateProjectFolderChildrenDTO = UpdateFolderChildrenDTO.builder()
//                .children(parentFolderChildren)
//                .build();


//        folderService.updateFolderChildren(parentFolder.getId(), updateProjectFolderChildrenDTO, userId);

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

    public Scenario updateresourceCollection(String id, String userId, JSONObject update) {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
        ResourceCollection resourceCollection = ResourceCollection.builder()
                .modelList((List<String>) update.get("modelList"))
                .dataList((List<String>) update.get("dataList")).build();
        scenario.setResourceCollection(resourceCollection);
//        update.updateTo(scenario);
        return scenarioRepository.save(scenario);
    }

    public Scenario updateScenarioInstance(String id, UpdateScenarioInstanceResourceDTO update) {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
        update.updateTo(scenario);
        return scenarioRepository.save(scenario);
    }

    public Scenario updateScenarioContainerId(String id, UpdateScenarioContainerIdDTO update) {
        Scenario scenario = scenarioRepository.findById(id).orElseThrow(MyException::noObject);
        update.updateTo(scenario);
        return scenarioRepository.save(scenario);
    }
}
