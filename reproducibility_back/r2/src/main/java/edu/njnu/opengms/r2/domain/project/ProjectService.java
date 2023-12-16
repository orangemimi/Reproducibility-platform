package edu.njnu.opengms.r2.domain.project;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.project.dto.AddProjectDTO;
import edu.njnu.opengms.r2.domain.project.dto.UpdateProjectDTO;
<<<<<<< Updated upstream
=======
import edu.njnu.opengms.r2.domain.scenario.Scenario;
import edu.njnu.opengms.r2.domain.scenario.ScenarioRepository;
import edu.njnu.opengms.r2.domain.scenario.ScenarioService;
>>>>>>> Stashed changes
import edu.njnu.opengms.r2.domain.user.User;
import edu.njnu.opengms.r2.domain.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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
    UserService userService;

<<<<<<< Updated upstream
=======
    @Autowired
    FolderService folderService;

    @Autowired
    ScenarioService scenarioService;

    @Autowired
    FolderRepository folderRepository;

>>>>>>> Stashed changes
    public Project get(String projectId) {
        return  projectRepository.findById(projectId).orElse(null);
    }

    public JSONObject getWithCreator(String projectId) {
        Project project= get(projectId);
        JSONObject object = new JSONObject();
        Optional.ofNullable(project)
                .map(x -> x.getCreatorId())
                .map(x -> {
                    JSONObject user = userService.getUserInfoById(x);
                    object.put("creatorName",user.get("name"));
                    return object;
                })
                .orElseGet( () -> {
                    return null;
                });

//        getScenario

        object.put("name",project.getName());
        object.put("description",project.getDescription());
        object.put("privacy",project.getPrivacy());
        object.put("creatorId",project.getCreatorId());
        object.put("scenario",project.getScenario());
        object.put("level",project.getLevel());
        object.put("starredCount",project.getStarredCount());
        object.put("watchedCount",project.getWatchedCount());
        object.put("picture",project.getPicture());
        object.put("tags",project.getTags());
        object.put("createTime",project.getCreateTime());
        object.put("updateTime",project.getUpdateTime());


        return object;
    }





    public Project create(String userId, AddProjectDTO add) {
        Project project = new Project();
        add.setCreatorId(userId);
        add.convertTo(project);
//        Creator creator = new Creator();
//        creator.setName(userName);
//        creator.setId(userId);
        Project result = projectRepository.insert(project);
        User user = userService.getUserInfoById(userId);

<<<<<<< Updated upstream
=======
        Project proj = projectRepository.insert(project);

        //create scenario
        Scenario exampleScenario = new Scenario();
        exampleScenario.setName("Example scenario");
        exampleScenario.setType("sequentModels");
        exampleScenario.setProjectId(proj.getId());
        Scenario newScenario = scenarioRepository.insert(exampleScenario);


        proj.setScenario(newScenario.getId());
        Project result= projectRepository.save(proj);

        //create project folder
        Folder mainFolder = folderRepository.findByCreatorIdAndName(userId, "Main");
        AddFolderDTO addFolderDTO = AddFolderDTO.builder()
                .level(1)
                .tagId(proj.getId())
                .name(proj.getName()+" --folder")
                .parent(mainFolder.getId())
                .build();

        Folder newProjectFolder =  folderService.create(addFolderDTO,userId);
//        Folder childFolder = folderRepository.insert(folder);
        List<String> mainFolderChildren = new ArrayList<String>();
        if(mainFolder.getChildren()==(null)){
            String id = newProjectFolder.getId();
            mainFolderChildren.add(id) ;
        } else{
            mainFolderChildren = mainFolder.getChildren();
            mainFolderChildren.add(newProjectFolder.getId());
        }

        UpdateFolderChildrenDTO updateMainFolderChildrenDTO = UpdateFolderChildrenDTO.builder()
                .children(mainFolderChildren)
                .build();
       folderService.updateFolderChildren(mainFolder.getId(),updateMainFolderChildrenDTO,userId);


        //create folderScenario
        AddFolderDTO addScenarioFolderDTO = AddFolderDTO.builder()
                .level(2)
                .tagId(newScenario.getId())
                .name("Example scenario --folder")
                .parent(newProjectFolder.getId())
                .build();

        Folder newScenarioFolder =  folderService.create(addScenarioFolderDTO,userId);
//        Folder childFolder = folderRepository.insert(folder);
        List<String> parentFolderChildren = new ArrayList<String>();
        String id = newScenarioFolder.getId();
        parentFolderChildren.add(id) ;

        UpdateFolderChildrenDTO updateProjectFolderChildrenDTO = UpdateFolderChildrenDTO.builder()
                .children(parentFolderChildren)
                .build();


        folderService.updateFolderChildren(newProjectFolder.getId(),updateProjectFolderChildrenDTO,userId);

        User user = userRepository.findById(userId).orElseThrow(MyException::noObject);
>>>>>>> Stashed changes
        if (user.getCreatedProjects()==null){
            List<String> newProjects =new ArrayList<>();
            newProjects.add(result.getId());
            user.setCreatedProjects(newProjects);
        }
        else {
            user.getCreatedProjects().add(result.getId());
        }

        userService.update(user);

        return result;

    }

    // get create & joined projects
    public List<Project> getMyProjects(String userId) {
        JSONObject userProjectInfo = userService.getUserProjectInfo(userId);
        List<String> createdProjects = (List<String>) userProjectInfo.get("createdProjects");
        List<String> joinedProjects = (List<String>) userProjectInfo.get("joinedProjects");
        List<Project> projectList = new ArrayList<>();
        if(createdProjects!=null) {
            for (String projectId : createdProjects) {
                projectList.add(get(projectId));
            }
        }
        if(joinedProjects!=null){
            for(String projectId : joinedProjects) {
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
        for(String s : createdProjects) {
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
        User user = userService.getUserInfoById(userId);
        Page<Project> projectList = projectRepository.findByPrivacyInOrCreatorId(privacyList, userId, pageable);
        return projectList;
    }

    public Project updateMembers(String userId, String projectId, Member update) {
        Project project = projectRepository.findByIdAndCreatorId(projectId, userId).orElseThrow(MyException::noObject);
//        if(project.getMembers().isEmpty()){
//            project.getMembers()= new ArrayList<>();
//        }
        project.getMemberList().add(update);


        User user = userService.getUserInfoById(update.getMemberId());
        user.getJoinedProjects().add(project.getId());
        userService.update(user);

        return projectRepository.save(project);
    }


    public Object getProjectAndUsers(String userId, String projectId) {
//        User reviewer = userService.getUserInfoById(userId);


        Project project = projectRepository.findById(projectId).orElse(null);
        User creator = userService.getUserInfoById(project.getCreatorId());


        List<Member> memberIdList = project.getMemberList();
        List<User> memberList = new ArrayList<>();

        JSONObject reviewer = new JSONObject();
        reviewer.put("userId", userId);

        JSONObject json = new JSONObject();
        json.put("project", project);
        json.put("creator", creator);
        if (!(memberIdList == null || memberIdList.size() == 0)) {
            for (int i = 0; i < memberIdList.size(); i++) {
                User member = userService.getUserInfoById(memberIdList.get(i).getMemberId());
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
