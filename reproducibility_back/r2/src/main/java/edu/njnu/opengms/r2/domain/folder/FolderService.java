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


    public Object getFolderByCreator(String userId) {
        Sort sort = Sort.by(new Sort.Order(Sort.Direction.DESC,"level"));

        List<Folder> folderList = folderRepository.findAllByCreatorId(userId,sort);
        Integer highestLevel =folderList.get(0).level;// 最高的
        ArrayList<JSONObject> newFolderList = new ArrayList<JSONObject>();
        for (Folder item : folderList) {
            JSONObject newFolder = new JSONObject();
            newFolder.put("name", item.getName());
            newFolder.put("id", item.getId());
            newFolder.put("level", item.getLevel());
            newFolder.put("parent", item.getParent());
            newFolder.put("childrenIds", item.getChildren());
            newFolder.put("isFolder", true);

            JSONArray newChildren = new JSONArray();
            if (item.getDataList() != null) {
                List<DataItem> newChildWithData = dataItemRepository.findAllByIdIn(item.getDataList());
                for(DataItem data: newChildWithData){
                    newChildren.add(data);
                }
                newFolder.put("dataList", newChildWithData);
            }
            newFolderList.add(newFolder);//把所有的folder筛选一遍，每一层的folder都有对应的childrenObject
        }

        for (JSONObject item : newFolderList) {
            JSONArray withObjectsFolder = new JSONArray();
            List<String> childrenIds = (List<String>) item.get("childrenIds");
            if(childrenIds!=null){
                for(String childId: childrenIds){
                    for(JSONObject item2 : newFolderList){
                        if(item2.get("id").equals(childId)){
                            withObjectsFolder.add(item2);
                        }
                    }
//                    JSONObject oneChild = newFolderList.stream()
//                            .filter(s->s.get("id").equals(childId))
//                            .findAny()
//                            .orElseThrow(MyException::noObject);//查找到所有的children folder

                }
            }
            List<DataItem> dataItemList = (List<DataItem>) item.get("dataList");
            if(dataItemList!=null){
                for(DataItem data: dataItemList){
                    withObjectsFolder.add(data);
                }
            }
            if(dataItemList!=null ||childrenIds!=null ){
                item.put("children",withObjectsFolder);
            }

        }
        List<JSONObject> returnFolders = newFolderList.stream()
                .filter(s->s.get("level").equals(0))
                .collect(Collectors.toList());
;
        return  returnFolders;

    }

    public Folder create(AddFolderDTO add, String userId) {
        Folder folder = new Folder();
        add.convertTo(folder);
        folder.setCreatorId(userId);
        return folderRepository.insert(folder);
    }

    public Object update(AddFolderDTO add, String userId) {
        Folder folder = new Folder();
        add.convertTo(folder);
        folder.setCreatorId(userId);
        return folderRepository.insert(folder);
    }
    public Folder updateFolderChildren(String id,UpdateFolderChildrenDTO add, String userId) {
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
}
