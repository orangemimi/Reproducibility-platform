package edu.njnu.opengms.r2.domain.folder;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.dataItem.DataItem;
import edu.njnu.opengms.r2.domain.dataItem.DataItemRepository;
import edu.njnu.opengms.r2.domain.folder.dto.AddFolderDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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


    public Object getFolderByCreator(String userId) {
        List<Folder> folderList = folderRepository.findAllByCreatorId(userId);
        JSONArray newFolderList = new JSONArray();

        for (Folder item : folderList) {
            JSONObject newFolder = new JSONObject();
            if (item.getDataList() != null) {
                List<String> dataItemIdList = item.getDataList();
                List<DataItem> dataItemList = new ArrayList<>();

                for (String dataId : dataItemIdList) {
                    DataItem dataItem = dataItemRepository.findById(dataId).orElseThrow(MyException::noObject);
                    dataItemList.add(dataItem);
                }

                newFolder.put("name", item.getName());
                newFolder.put("children", item.getChildren());
                newFolder.put("id", item.getId());
                newFolder.put("level", item.getLevel());
                newFolder.put("parent", item.getParent());
                newFolder.put("dataList", dataItemList);
                newFolder.put("isFolder", true);
                newFolderList.add(newFolder);
            } else {
                newFolder.put("name", item.getName());
                newFolder.put("children", item.getChildren());
                newFolder.put("id", item.getId());
                newFolder.put("level", item.getLevel());
                newFolder.put("parent", item.getParent());
                newFolder.put("isFolder", true);
                newFolderList.add(newFolder);
            }
        }

        return newFolderList;

    }

    public Object create(AddFolderDTO add, String userId) {
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
