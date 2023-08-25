package edu.njnu.opengms.r2.domain.programol.Service;


import cn.hutool.system.UserInfo;
import edu.njnu.opengms.r2.domain.programol.Dao.ProjectDao;
import edu.njnu.opengms.r2.domain.programol.Dao.WorkSpaceDao;
import edu.njnu.opengms.r2.domain.programol.Project;
import edu.njnu.opengms.r2.domain.programol.WorkSpace;
import edu.njnu.opengms.r2.domain.programol.dto.Option;
import edu.njnu.opengms.r2.domain.programol.dto.WorkSpaceDTO;
import edu.njnu.opengms.r2.domain.user.User;
import edu.njnu.opengms.r2.domain.user.UserRepository;
import edu.njnu.opengms.r2.utils.JsonResult;
import edu.njnu.opengms.r2.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * @Author TRY
 * @Date 2023/3/29 11:10
 * @Version 1.0
 */
@Slf4j
@Service
public class WorkSpaceService {
    @Autowired
    WorkSpaceDao workSpaceDao;
    @Autowired
    UserRepository userDao;
    @Autowired
    ProjectDao projectDao;
//    @Value("W:/YangtzeDataStore/workspace")
    @Value("E:/workspace")
    private String workSpaceDir;


    public JsonResult OpenworkSpace(String userId){
        WorkSpace workspace = workSpaceDao.findByUserId(userId);
        Optional<User> userDB = userDao.findById(userId);
        User user=null;
        if(userDB.isPresent()){
            user = userDB.get();
        }

        String name = user.getName();
        if (workspace==null){
            WorkSpace workSpace=new WorkSpace();
            workSpace.setUserId(userId);
            workSpace.setProjectCount(0);
            workSpace.setUserName(name);
            workSpace.setWorkspacePath(workSpaceDir+'/'+userId);
            File folder = new File(workSpaceDir+'/'+userId);
            if (!folder.isDirectory()) {
                folder.mkdirs();
            }
            workSpaceDao.save(workSpace);
            //docker的操作
            WorkSpaceDTO workSpaceDTO=new WorkSpaceDTO();
            workSpaceDTO.setUserName(name);
            ArrayList<Option> options = new ArrayList<>();
            workSpaceDTO.setProjectOption(options);
            return ResultUtils.success(workSpaceDTO);
        }
        else {
            //docker的操作


            WorkSpaceDTO workSpaceDTO=new WorkSpaceDTO();
            workSpaceDTO.setUserName(name);
            ArrayList<Option> options = new ArrayList<>();
            List<Project> projects=projectDao.findByWorkspace(userId);
            for (Project project : projects) {
                Option option = new Option();
                option.setValue(project.getId());
                option.setLabel(project.getProjectName());
                options.add(option);
            }
            workSpaceDTO.setProjectOption(options);
            return ResultUtils.success(workSpaceDTO);
        }
    }
}
