package edu.njnu.opengms.r2.domain.project;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.folder.Folder;
import edu.njnu.opengms.r2.domain.folder.FolderRepository;
import edu.njnu.opengms.r2.domain.folder.FolderService;
import edu.njnu.opengms.r2.domain.folder.dto.AddFolderDTO;
import edu.njnu.opengms.r2.domain.folder.dto.UpdateFolderChildrenDTO;
import edu.njnu.opengms.r2.domain.project.dto.AddProjectDTO;
import edu.njnu.opengms.r2.domain.project.dto.UpdateProjectDTO;
import edu.njnu.opengms.r2.domain.scenario.Scenario;
import edu.njnu.opengms.r2.domain.scenario.ScenarioRepository;
import edu.njnu.opengms.r2.domain.scenario.ScenarioService;
import edu.njnu.opengms.r2.domain.user.User;
import edu.njnu.opengms.r2.domain.user.UserRepository;
import edu.njnu.opengms.r2.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/21 11:20
 * @modified By：
 * @version: 1.0.0
 */
@Service
public class ProjectService {
    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ScenarioRepository scenarioRepository;

    @Autowired
    UserService userService;

    @Autowired
    FolderService folderService;

    @Autowired
    ScenarioService scenarioService;

    @Autowired
    FolderRepository folderRepository;

    public Project get(String projectId) {
        return projectRepository.findById(projectId).orElse(null);
    }

    public JSONObject getWithCreator(String projectId) {
        Project project = get(projectId);

        JSONObject object = new JSONObject();
        JSONObject user = userService.getUserInfoById(project.getCreatorId());
        object.put("creatorName", user.get("name"));

        List<JSONObject> jsonObjectList = new ArrayList<>();
        for (String i : project.getScenarioList()) {
            JSONObject scenario = scenarioService.getScenario(i);
            jsonObjectList.add(scenario);
        }


        object.put("name", project.getName());
        object.put("description", project.getDescription());
        object.put("privacy", project.getPrivacy());
        object.put("creatorId", project.getCreatorId());
        object.put("scenarioList", project.getScenarioList());
        object.put("scenarioObjectList", jsonObjectList);
        object.put("level", project.getLevel());
        object.put("starredCount", project.getStarredCount());
        object.put("watchedCount", project.getWatchedCount());
        object.put("picture", project.getPicture());
        object.put("tags", project.getTags());
        object.put("createTime", project.getCreateTime());
        object.put("updateTime", project.getUpdateTime());


        return object;
    }


    public Project create(String userId, AddProjectDTO add) {
        Project project = new Project();
        add.setCreatorId(userId);
        add.convertTo(project);
        try{
            Project proj = projectRepository.insert(project);
            proj.setScenarioList(new ArrayList<>());
            Project result = projectRepository.save(proj);
              //create example scenario
//            Scenario exampleScenario = new Scenario();
//            exampleScenario.setName("Example scenario");
//            exampleScenario.setType("sequentModels");
//            exampleScenario.setProjectId(proj.getId());
//            exampleScenario.setCreator(userId);
//            Scenario newScenario = scenarioRepository.insert(exampleScenario);

              //add example scenario to project
//            List<String> temp = new ArrayList<>();
//            temp.add(newScenario.getId());
//            proj.setScenarioList(temp);
//            Project result = projectRepository.save(proj);

            //create project folder
            Folder mainFolder = folderRepository.findByCreatorIdAndName(userId, "Main");
            AddFolderDTO addFolderDTO = AddFolderDTO.builder()
                    .level(1)
                    .tagId(proj.getId())
                    .name(proj.getName() + " --folder")
                    .parent(mainFolder.getId())
                    .build();

            Folder newProjectFolder = folderService.create(addFolderDTO, userId);
//            folderRepository.insert(newProjectFolder);

            // 将新创建的项目文件夹添加到文件夹系统中
            List<String> mainFolderChildren = new ArrayList<String>();
            if (mainFolder.getChildren() == (null)) {
                mainFolderChildren.add(newProjectFolder.getId());
            } else {
                mainFolderChildren = mainFolder.getChildren();
                mainFolderChildren.add(newProjectFolder.getId());
            }

            UpdateFolderChildrenDTO updateMainFolderChildrenDTO = UpdateFolderChildrenDTO.builder()
                    .children(mainFolderChildren)
                    .build();
            folderService.updateFolderChildren(mainFolder.getId(), updateMainFolderChildrenDTO, userId);


            //create folderScenario
//            AddFolderDTO addScenarioFolderDTO = AddFolderDTO.builder()
//                    .level(2)
//                    .tagId(newScenario.getId())
//                    .name("Example scenario --folder")
//                    .parent(newProjectFolder.getId())
//                    .build();
//            Folder newScenarioFolder = folderService.create(addScenarioFolderDTO, userId);

            // 更新项目文件夹的子文件夹列表
//            List<String> parentFolderChildren = new ArrayList<String>();
//            String id = newScenarioFolder.getId();
//            parentFolderChildren.add(id);
//            UpdateFolderChildrenDTO updateProjectFolderChildrenDTO = UpdateFolderChildrenDTO.builder()
//                    .children(parentFolderChildren)
//                    .build();
//            folderService.updateFolderChildren(newProjectFolder.getId(), updateProjectFolderChildrenDTO, userId);

            User user = userRepository.findById(userId).orElseThrow(MyException::noObject);
            if (user.getCreatedProjects() == null) {
                List<String> newProjects = new ArrayList<>();
                newProjects.add(proj.getId());
                user.setCreatedProjects(newProjects);
            } else {
                user.getCreatedProjects().add(proj.getId());
            }

            userService.update(user);

            return proj;
        }catch(Exception e ){
            System.out.println(e.getMessage());
            return null;
        }
    }

    public Object folk(JSONObject jsonObject, String userId, String userName, String projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(MyException::noObject);
        AddProjectDTO add = new AddProjectDTO();
        add.setCreatorId(userId);
        add.setName(project.name);
        add.setDescription(project.description);
        add.setForkingProjectId(projectId);
        add.setPrivacy("public");
        add.setTags(project.tags);
        add.setScenarioList(project.scenarioList);

        Project project2 = new Project();
        add.convertTo(project2);

        return projectRepository.save(project2);

    }

    // get create & joined projects
    public List<Project> getMyProjects(String userId) {
        JSONObject userProjectInfo = userService.getUserProjectInfo(userId);
        List<String> createdProjects = (List<String>) userProjectInfo.get("createdProjects");
        List<String> joinedProjects = (List<String>) userProjectInfo.get("joinedProjects");
        List<Project> projectList = new ArrayList<>();
        if (createdProjects != null) {
            for (String projectId : createdProjects) {
                projectList.add(get(projectId));
            }
        }
        if (joinedProjects != null) {
            for (String projectId : joinedProjects) {
                projectList.add(get(projectId));
            }
        }
        return projectList;
    }

    /**
     * @Description:分页查询projects
     * @Author: Yiming
     * @Date: 2022/1/10
     */
    public Page<Project> getPublicProjects(int currentPage, int pageSize) {
        PageRequest pageable = PageRequest.of(currentPage, pageSize, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Project> projects = projectRepository.findByPrivacy("public", pageable);
        return projects;
    }


    // get only create projects
    public JSONArray getProjectsCreatedByMe(String userId) {
        JSONObject userProjectInfo = userService.getUserProjectInfo(userId);
        List<String> createdProjects = (List<String>) userProjectInfo.get("createdProjects");
        JSONArray jsonArray = new JSONArray();
        for (String s : createdProjects) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("projectId", s);
            jsonObject.put("name", projectRepository.findById(s).orElseThrow(MyException::noObject).getName());
            jsonArray.add(jsonObject);
        }
        return jsonArray;
    }

    public Page<Project> getAllProjects(String userId, int currentPage, int pagesize) {
        PageRequest pageable = PageRequest.of(currentPage, pagesize);
        List<String> privacyList = Arrays.asList("public", "private");
//        User user = userRepository.findById(userId).orElseThrow(MyException::noObject)
        Page<Project> projectList = projectRepository.findByPrivacyInOrCreatorId(privacyList, userId, pageable);
        return projectList;
    }

    public Project updateMembers(String userId, String projectId, Member update) {
        Project project = projectRepository.findByIdAndCreatorId(projectId, userId).orElseThrow(MyException::noObject);
//        if(project.getMembers().isEmpty()){
//            project.getMembers()= new ArrayList<>();
//        }
        project.getMemberList().add(update);


        User user = userRepository.findById(userId).orElseThrow(MyException::noObject);
        user.getJoinedProjects().add(project.getId());
        userService.update(user);

        return projectRepository.save(project);
    }


    public Object getProjectAndUsers(String userId, String projectId) {
//        User reviewer = userService.getUserInfoById(userId);


        Project project = projectRepository.findById(projectId).orElse(null);
        User user = userRepository.findById(userId).orElseThrow(MyException::noObject);


        List<Member> memberIdList = project.getMemberList();
        List<User> memberList = new ArrayList<>();

        JSONObject reviewer = new JSONObject();
        reviewer.put("userId", userId);

        JSONObject json = new JSONObject();
        json.put("project", project);
        json.put("creator", user);
        if (!(memberIdList == null || memberIdList.size() == 0)) {
            for (int i = 0; i < memberIdList.size(); i++) {
                User member = userRepository.findById(memberIdList.get(i).getMemberId()).orElseThrow(MyException::noObject);

                memberList.add(member);

            }
            json.put("memberList", memberList);
        }

        return json;
    }

    public int getStarredCount(String projectId) {
        Project project = projectRepository.findById(projectId).orElseThrow(MyException::noObject);
        return project.getStarredCount();
    }


    public Object update(String projectId, String userId, UpdateProjectDTO update) {
        Project project = projectRepository.findByIdAndCreatorId(projectId, userId).orElseThrow(MyException::noObject);
        update.updateTo(project);
        return projectRepository.save(project);
    }


}
