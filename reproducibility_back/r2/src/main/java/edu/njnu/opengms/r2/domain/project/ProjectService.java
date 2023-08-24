package edu.njnu.opengms.r2.domain.project;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.project.dto.AddProjectDTO;
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

    public Project get(String projectId) {
        return projectRepository.findById(projectId).orElse(null);
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
        for(String projectId : createdProjects) {
            projectList.add(get(projectId));
        }
        for(String projectId : joinedProjects) {
            projectList.add(get(projectId));
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


}
