package edu.njnu.opengms.r2.domain.folder;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.dataItem.DataItem;
import edu.njnu.opengms.r2.domain.dataItem.DataItemRepository;
import edu.njnu.opengms.r2.domain.folder.dto.AddFolderDTO;
import edu.njnu.opengms.r2.domain.folder.dto.UpdateFolderChildrenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author ：Zhiyi
 * @Date ：2023/9/1 19:36
 * @modified By：
 * @version: 1.0.0
 */
@Service
public class FolderService {

    @Autowired
    FolderRepository folderRepository;

    @Autowired
    DataItemRepository dataItemRepository;


    //    public static JSONObject getFolder(Folder folder){
//        if(!folder.getLevel().equals(0)){
//            JSONArray childrenFolder = new JSONArray();
//
//
//        }
//        return;
//    }
    public Object getFolderByScenarioId(String scenarioId) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "level"));

        Folder folder = folderRepository.findByTagId(scenarioId);
        ArrayList<JSONObject> newFolderList = new ArrayList<JSONObject>();
        JSONObject newFolder = new JSONObject();
        newFolder.put("name", folder.getName());
        newFolder.put("id", folder.getId());
        newFolder.put("level", folder.getLevel());
        newFolder.put("parent", folder.getParent());
        newFolder.put("childrenIds", folder.getChildren());
        newFolder.put("isFolder", true);
        newFolder.put("tagId", folder.getTagId());
        //如果文件夹里有数据，就把数据也传出去
        JSONArray newChildren = new JSONArray();
        if (folder.getDataList() != null && folder.getDataList().size() != 0) {
            List<DataItem> newChildWithData = dataItemRepository.findAllByIdIn(folder.getDataList());
            for (DataItem data : newChildWithData) {
                newChildren.add(data);
            }
            newFolder.put("dataItemList", newChildWithData);
        }
        newFolderList.add(newFolder);//把所有的folder筛选一遍，每一层的folder都有对应的childrenObject


        for (JSONObject item : newFolderList) {
            List<JSONObject> withObjectsFolder = new ArrayList<>();
            List<String> childrenIds = (List<String>) item.get("childrenIds");
            if (childrenIds != null && childrenIds.size() != 0) {
                for (String childId : childrenIds) {
                    for (JSONObject item2 : newFolderList) {
                        if (item2.get("id").equals(childId)) {
                            withObjectsFolder.add(item2);
                        }
                    }
                }
            }
            JSONArray dataItemList = (JSONArray) item.get("dataItemList");
            if (dataItemList != null && dataItemList.size() != 0) {
                for (int i = 0; i < dataItemList.size(); i++) {
                    JSONObject data = (JSONObject) dataItemList.get(i);
                    withObjectsFolder.add(data);
                }
            }
            if (dataItemList != null || (childrenIds != null && childrenIds.size() != 0)) {
                item.put("children", withObjectsFolder);
            }

        }

        return newFolderList;
    }


    public Object getFolderByCreator(String userId) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "level"));

        List<Folder> folderList = folderRepository.findAllByCreatorId(userId, sort);
//        Integer highestLevel =folderList.get(0).level;// 最高的
        ArrayList<JSONObject> newFolderList = new ArrayList<JSONObject>();
        for (Folder item : folderList) {
            JSONObject newFolder = new JSONObject();
            newFolder.put("name", item.getName());
            newFolder.put("id", item.getId());
            newFolder.put("level", item.getLevel());
            newFolder.put("parent", item.getParent());
            newFolder.put("childrenIds", item.getChildren());
            newFolder.put("isFolder", true);
            newFolder.put("tagId", item.getTagId());
            //如果文件夹里有数据，就把数据也传出去
            JSONArray newChildren = new JSONArray();
            if (item.getDataList() != null && item.getDataList().size() != 0) {
                List<DataItem> newChildWithData = dataItemRepository.findAllByIdIn(item.getDataList());
                for (DataItem data : newChildWithData) {
                    newChildren.add(data);
                }
                newFolder.put("dataItemList", newChildWithData);
            }
            newFolderList.add(newFolder);//把所有的folder筛选一遍，每一层的folder都有对应的childrenObject
        }

        for (JSONObject item : newFolderList) {
            List<JSONObject> withObjectsFolder = new ArrayList<>();
            List<String> childrenIds = (List<String>) item.get("childrenIds");
            if (childrenIds != null && childrenIds.size() != 0) {
                for (String childId : childrenIds) {
                    for (JSONObject item2 : newFolderList) {
                        if (item2.get("id").equals(childId)) {
                            withObjectsFolder.add(item2);
                        }
                    }
//                    JSONObject oneChild = newFolderList.stream()
//                            .filter(s->s.get("id").equals(childId))
//                            .findAny()
//                            .orElseThrow(MyException::noObject);//查找到所有的children folder

                }
            }
            JSONArray dataItemList = (JSONArray) item.get("dataItemList");
            if (dataItemList != null && dataItemList.size() != 0) {
                for (int i = 0; i < dataItemList.size(); i++) {
                    JSONObject data = (JSONObject) dataItemList.get(i);
                    withObjectsFolder.add(data);
                }
            }
            if (dataItemList != null || (childrenIds != null && childrenIds.size() != 0)) {
                item.put("children", withObjectsFolder);
            }

        }
        List<JSONObject> returnFolders = newFolderList.stream()
                .filter(s -> s.get("level").equals(0))
                .collect(Collectors.toList());
        return returnFolders;

    }

    public Folder create(AddFolderDTO add, String userId) {
        Folder folder = new Folder();
        add.convertTo(folder);
        folder.setCreatorId(userId);
        Folder newFolder = folderRepository.insert(folder);
        if (newFolder.getParent() != "0") {
            Folder parentFolder = folderRepository.findById(newFolder.getParent()).orElseThrow(MyException::noObject);

            List childrenIds;
            if (parentFolder.getChildren() != null && parentFolder.getChildren().size() != 0) {
                childrenIds = parentFolder.getChildren();
                childrenIds.add(newFolder.getId());

            } else {
                childrenIds = new JSONArray();
                childrenIds.add(newFolder.getId());

            }
            UpdateFolderChildrenDTO update = UpdateFolderChildrenDTO.builder()
                    .children(childrenIds)
                    .build();

            updateFolderChildren(newFolder.getParent(), update, userId);
        }

        return newFolder;
    }

    public Object update(AddFolderDTO add, String userId) {
        Folder folder = new Folder();
        add.convertTo(folder);
        folder.setCreatorId(userId);
        return folderRepository.insert(folder);
    }

    public Folder updateFolderChildren(String id, UpdateFolderChildrenDTO add, String userId) {
        Folder folder = folderRepository.findById(id).orElseThrow(MyException::noObject);
        add.convertTo(folder);
        folder.setCreatorId(userId);
        return folderRepository.save(folder);
    }


    public Folder updataDataList(String id, String addedDataId) {
        Folder folder = folderRepository.findById(id).orElseThrow(MyException::noObject);
        List<String> folderDataList = new ArrayList<>();
        if (folder.getDataList() == null || folder.getDataList().size() == 0) {
            folderDataList.add(addedDataId);
        } else {
            folderDataList = folder.getDataList();
            folderDataList.add(addedDataId);
        }

        folder.setDataList(folderDataList);
        return folderRepository.save(folder);
    }

    public Folder replaceDataList(String id, String addedDataId, String oldDucumentId) {
        Folder folder = folderRepository.findById(id).orElseThrow(MyException::noObject);
        List<String> folderDataList = new ArrayList<>();
        if (folder.getDataList() == null || folder.getDataList().size() == 0) {
            folderDataList.add(addedDataId);
        } else {
            for (String dataId : folder.getDataList()) {
                if (dataId.equals(oldDucumentId)) {
                    folderDataList.add(addedDataId);
                } else {
                    folderDataList.add(dataId);
                }
            }
        }

        folder.setDataList(folderDataList);
        return folderRepository.save(folder);
    }


    public String getFolderIdByDataItemId(String userId, String dataId) {
        // 先拿到user对应的folders
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC, "level"));
        List<Folder> folderList = folderRepository.findAllByCreatorId(userId, sort);

        //遍历folders，梳理
        for (Folder item : folderList) {
            if (item.getDataList() != null && !item.getDataList().isEmpty()) {
                List<String> dataItemIds = item.getDataList();
                for (String dataItemId : dataItemIds) {
                    if (dataItemId.equals(dataId)) {
                        return item.getId();
                    }
                }
            }
        }
        return null;
    }


}
