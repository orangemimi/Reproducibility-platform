package edu.njnu.opengms.r2.domain.programol.Service;


import edu.njnu.opengms.r2.domain.programol.Dao.ProgramFileDao;
import edu.njnu.opengms.r2.domain.programol.Dao.ProjectDao;
import edu.njnu.opengms.r2.domain.programol.Dao.WorkSpaceDao;
import edu.njnu.opengms.r2.domain.programol.ProgramFile;
import edu.njnu.opengms.r2.domain.programol.Project;
import edu.njnu.opengms.r2.domain.programol.WorkSpace;
import edu.njnu.opengms.r2.domain.programol.dto.CreateProgramFileDTO;
import edu.njnu.opengms.r2.domain.programol.dto.CreateProjectDTO;
import edu.njnu.opengms.r2.domain.programol.dto.ProgramFileDTO;
import edu.njnu.opengms.r2.domain.programol.dto.projectDTOO;
import edu.njnu.opengms.r2.utils.FileUtils;
import edu.njnu.opengms.r2.utils.JsonResult;
import edu.njnu.opengms.r2.utils.ResultUtils;
import edu.njnu.opengms.r2.zaqizaba.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
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
public class PrOjectService {
    @Autowired
    ProjectDao projectDao;
    @Autowired
    WorkSpaceDao workSpaceDao;
    @Autowired
    ProgramFileDao programFileDao;

    @Autowired
    DockerService dockerService;
//    @Value("W:/YangtzeDataStore/workspace")
    @Value("E:/workspace")
    private String workSpaceDir;

    @Autowired
    WebSocketServer webSocketService;


//    @Autowired
//    DockerClient dockerClient;

    @Autowired
    ProgramFileService programFileService;

    public JsonResult CreateProject(CreateProjectDTO createProjectDTO){
        try {
            WorkSpace workSpace = workSpaceDao.findByUserId(createProjectDTO.getUserId());
            List<String> projectNames = workSpace.getProjectNames();
            if (projectNames!=null){
                for (String name : projectNames) {
                    if (name.equals(createProjectDTO.getProjectName())){
                        return ResultUtils.error("创建失败,已存在同名项目");
                    }
                }
            }else {projectNames=new ArrayList<String>();}
            String ProjectPath=workSpaceDir+'/'+createProjectDTO.getUserId()+'/'+createProjectDTO.getProjectName();
            File folder = new File(ProjectPath);
            if (!folder.isDirectory()) {
                folder.mkdirs();
            }
            //创建的project对象
            Project project=new Project();
            //设置project的属性
            BeanUtils.copyProperties(createProjectDTO,project);
            project.setProjectPath(workSpaceDir+'/'+createProjectDTO.getUserId()+'/'+createProjectDTO.getProjectName());
            project.setIfReq(true);
            project.setRequirementPath(workSpaceDir+'/'+createProjectDTO.getUserId()+'/'+createProjectDTO.getProjectName()+'/'+"requirements.txt");
            project.setPyPath(workSpaceDir+'/'+createProjectDTO.getUserId()+'/'+createProjectDTO.getProjectName()+'/'+"main.py");
            //在workspace中添加相应的project
            workSpace.setProjectCount(workSpace.getProjectCount()+1);
            projectNames.add(createProjectDTO.getProjectName());
            workSpace.setProjectNames(projectNames);
            //创建容器
            String containerid = dockerService.createContainer(project.getPythonVersion(), project.getId(),project.getProjectPath(),project.getProjectName());
            project.setContainerId(containerid);
            dockerService.startContainer(containerid);
            workSpaceDao.save(workSpace);
            //创建项目的文件夹
            CreateProgramFileDTO ProjectFolderDTO = new CreateProgramFileDTO(createProjectDTO.getProjectName(), ProjectPath, true, createProjectDTO.getProjectName(),createProjectDTO.getProjectName(), createProjectDTO.getUserId());
            programFileService.CreateFile(ProjectFolderDTO);
            //这里获取了根文件夹
            ProgramFile projectFolder = programFileDao.findProgramFileByFilePath(project.getProjectPath());
            project.setProjectFileId(projectFolder.getId());
            projectDao.save(project);
            //创建两个必要的文件分别是main.py和requirements.txt
            CreateProgramFileDTO createMainPythonFileDTO = new CreateProgramFileDTO("main.py",ProjectPath+"/main.py",false, createProjectDTO.getProjectName(),projectFolder.getId(),createProjectDTO.getUserId());
            CreateProgramFileDTO createReqFileDTO = new CreateProgramFileDTO("requirements.txt",ProjectPath+"/requirements.txt",false,createProjectDTO.getProjectName(),projectFolder.getId(),createProjectDTO.getUserId());
            programFileService.CreateFile(createMainPythonFileDTO);
            programFileService.CreateFile(createReqFileDTO);
            //最后返回给前端的是下面的
            projectDTOO projectDTO = new projectDTOO();
            BeanUtils.copyProperties(project,projectDTO);
            ArrayList<ProgramFileDTO> programFileDTOS = new ArrayList<>();
            programFileDTOS.add(build(projectFolder.getId()));
            projectDTO.setProgramFileDTO(programFileDTOS);
            return ResultUtils.success(projectDTO);
        } catch (BeansException e) {
            e.printStackTrace();
            return ResultUtils.error("容器创建失败");
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResultUtils.error("容器创建失败");
        }
    }
    //根据各个文件的父子关系生成一个tree返回给前端
    public ProgramFileDTO build(String fileId) {
        Optional<ProgramFile> parentFile = programFileDao.findById(fileId);

        if (!parentFile.isPresent()) {
            return null;
        }
        ProgramFile file = parentFile.get();

        if (!file.isFolder()) {
            return new ProgramFileDTO(file.getId(), "file", file.getFilePath(), file.getFileName());
        }

        List<ProgramFileDTO> children = new ArrayList<>();
        List<ProgramFile> childrenFiles = programFileDao.findProgramFilesByParentId(fileId);
        for (ProgramFile programFile : childrenFiles) {
            ProgramFileDTO childNode = build(programFile.getId());
            children.add(childNode);
        }



        return new ProgramFileDTO(fileId,"folder", file.getFilePath(), file.getFileName(),children);
    }
    //打开项目
    public JsonResult OpenProject(String projectId) {
//        webSocketService.sendMessageByProject("123","aasdasdasdasd");
        Optional<Project> projectDB = projectDao.findById(projectId);
        if (!projectDB.isPresent()){
            return ResultUtils.error("打开项目失败");
        }
        Project project = projectDB.get();
        ProgramFile projectfile = programFileDao.findProgramFileByFilePath(project.getProjectPath());
//        String ProjectPath=project.getProjectPath();
        projectDTOO projectDTO = new projectDTOO();
        BeanUtils.copyProperties(project,projectDTO);
        ArrayList<ProgramFileDTO> programFileDTOS = new ArrayList<>();
        programFileDTOS.add(build(projectfile.getId()));
        projectDTO.setProgramFileDTO(programFileDTOS);
        if (projectDTO!=null){
            //启动容器
            try {
                String containerid;
                if (project.getContainerId()==null){
                    containerid = dockerService.createContainer(project.getPythonVersion(), project.getId(),project.getProjectPath(),project.getProjectName());
                    project.setContainerId(containerid);
                    projectDao.save(project);
                }
                else {
                    containerid=project.getContainerId();
                }
//                dockerService.checkstate();
//                System.out.println(inspectContainerResponse.getState());
//                dockerService.startContainer(containerid);
                //容器是否在运行中？
                boolean checkstate = dockerService.checkstate(containerid);
                if (!checkstate){
                    //不在运行的话就打开
                    dockerService.startContainer(containerid);
                }
                return ResultUtils.success(projectDTO);
            } catch (InterruptedException e) {
                e.printStackTrace();
                return ResultUtils.error("创建容器失败");
            }
        }
        else {return ResultUtils.error("打开项目失败");}
    }
    //删除项目
    public JsonResult DeleteProject(String projectId) {
        Optional<Project> projectDB = projectDao.findById(projectId);
        if (!projectDB.isPresent()){
            return ResultUtils.error("删除项目失败");
        }
        Project project = projectDB.get();
        WorkSpace workSpace=workSpaceDao.findByUserId(project.getUserId());
        Integer integer = programFileDao.deleteAllByProjectNameAndUserId(project.getProjectName(),project.getUserId());
        System.out.println(integer);
        List<String> projectNames = workSpace.getProjectNames();
        projectNames.remove(project.getProjectName());
        workSpace.setProjectNames(projectNames);
        workSpace.setProjectCount(workSpace.getProjectCount()-1);
        workSpaceDao.save(workSpace);
        String projectPath = project.getProjectPath();
        if (project.getContainerId()!=null){
            String containerId=project.getContainerId();
            try {
                dockerService.stopContainer(containerId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }finally {
                try {
                    dockerService.removeContainer(containerId);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        try {
            FileUtils.deleteFile(projectPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        projectDao.deleteById(projectId);
        return ResultUtils.success("删除成功");
    }
    //下载python依赖
    public JsonResult DownloadDep(String projectId){
        Optional<Project> projectDB = projectDao.findById(projectId);
        if (!projectDB.isPresent()){
            return ResultUtils.error("项目不存在");
        }
        Project project = projectDB.get();
        String containerId = project.getContainerId();
        try {
            dockerService.downloadDep(containerId,projectId);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResultUtils.error("依赖下载失败");
        }
        return ResultUtils.success("下载依赖成功");
    }
    //运行demo
    public JsonResult RunDemo(String projectId){
        Optional<Project> projectDB = projectDao.findById(projectId);
        if (!projectDB.isPresent()){
            return ResultUtils.error("项目不存在");
        }
        Project project = projectDB.get();
        String containerId = project.getContainerId();
        if (!dockerService.checkstate(containerId)){
            return ResultUtils.error("运行出错");
        }
        try {
            dockerService.rundemo(containerId,projectId);
            scanAndSave(project.getProjectPath(),project.getProjectFileId(),project.getProjectName(),programFileDao,project.getUserId());
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResultUtils.error("运行出错");
        }
        return ResultUtils.success("运行成功");
    }

    public JsonResult StopDemo(String projectId){
        Optional<Project> projectDB = projectDao.findById(projectId);
        if (!projectDB.isPresent()){
            return ResultUtils.error("项目不存在");
        }
        Project project = projectDB.get();
        String containerId = project.getContainerId();
        try {
            dockerService.stopdemo(containerId,projectId);
        } catch (InterruptedException e) {
            e.printStackTrace();
            return ResultUtils.error("停止失败");
        }
        return ResultUtils.success("已停止");
    }

    public static void scanAndSave(String folderPath, String parentId,String projectName,ProgramFileDao programFileDao,String userId) {
        File folder = new File(folderPath);
        if (!folder.exists() || !folder.isDirectory()) {
            throw new IllegalArgumentException("Invalid folder path: " + folderPath);
        }

        File[] files = folder.listFiles();
        if (files == null) {
            return;
        }

        for (File file : files) {
            String filePath = file.getAbsolutePath().replace("\\","/");
            String fileType = file.isDirectory() ? "Folder" : "File";
            ProgramFile programFileByFilePath = programFileDao.findProgramFileByFilePath(filePath);
            // Check if file already exists in the database
            if ( programFileByFilePath!= null) {

                if (file.isDirectory()) {
                    scanAndSave(filePath, programFileByFilePath.getId(),projectName,programFileDao,userId);
                }
                continue;
            }
            // Create a new ProgramFile object and save it to the database
            ProgramFile programFile = new ProgramFile();
            programFile.setParentId(parentId);
            programFile.setFilePath(filePath);
            programFile.setFolder(fileType.equals("Folder")?true:false);
            programFile.setFileName(file.getName());
            programFile.setProjectName(projectName);
            programFile.setUserId(userId);
            programFileDao.save(programFile);

            // If the current file is a directory, recursively scan and save its contents
            if (file.isDirectory()) {
                scanAndSave(filePath, programFile.getId(),projectName,programFileDao,userId);
            }
        }
    }

    public JsonResult StopContainer(String projectId) {
        Optional<Project> projectDB = projectDao.findById(projectId);
        if (!projectDB.isPresent()){
            return ResultUtils.error("项目不存在");
        }
        Project project = projectDB.get();
        String containerId = project.getContainerId();
        try {
            dockerService.stopContainer(containerId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }finally {
            try {
                dockerService.removeContainer(containerId);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        project.setContainerId(null);
        projectDao.save(project);
        return ResultUtils.success("容器已停止");
    }
}
