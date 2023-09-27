package edu.njnu.opengms.r2.domain.user;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.enums.ResultEnum;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.common.utils.JwtUtils;
import edu.njnu.opengms.r2.domain.folder.FolderService;
import edu.njnu.opengms.r2.domain.folder.dto.AddFolderDTO;
import edu.njnu.opengms.r2.domain.model.ModelRepository;
import edu.njnu.opengms.r2.domain.project.Project;
import edu.njnu.opengms.r2.domain.project.ProjectRepository;
import edu.njnu.opengms.r2.remote.UserServiceServie;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.*;

import static edu.njnu.opengms.r2.utils.Utils.filterUserInfo;
import static edu.njnu.opengms.r2.utils.Utils.filterUserInfoProjects;


/**
 * @Author ：Zhiyi
 * @Date ：2020/12/4 14:29
 * @modified By：
 * @version: 1.0.0
 */
@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    UserServiceServie userServiceServie;

    @Autowired
    FolderService folderService;

    @Autowired
    ModelRepository modelRepository;

    @Autowired
    ProjectRepository projectRepository;

    public User findByName(String s) {
        return userRepository.findByName(s).orElse(null);
    }

    public JSONArray getUserInfoLike(String value) {
        List<User> userList =userRepository.findByEmailLike(value);
        return filterUserInfo(userList);
    }





    /*
    * 目的：在本地数据库注册，同时在用户服务器注册
    * 1.检查email是否已经存在
    * 2.注册
    * */
    public User register(String name, String email, String password) {

        //用户邮箱已经被注册的情况,抛出自定义异常
        if (userRepository.findByEmail(email).isPresent()) {
            throw new MyException(ResultEnum.EXIST_OBJECT);
        }


        User user=User.builder().name(name).email(email).password(password).build();

        User newUser =  userRepository.insert(user);

        AddFolderDTO add = new AddFolderDTO();
        add.setName("Main");
        add.setParent("0");
        add.setLevel("0");
        folderService.create(add,newUser.id);

        System.out.println(user.toString());
        return newUser;
    }

    public JSONObject login(String email, String password) {

        User userFromDB = userRepository.findByEmail(email).orElseThrow(MyException::noObject);
        if(userFromDB.getPassword().equals(password)){
            String jwtToken = JwtUtils.generateToken(userFromDB.getId(),userFromDB.getName(), email, password);
            JSONObject json = new JSONObject();
            json.put("name", userFromDB.getName());
            json.put("userId", userFromDB.getId());
//            json.put("avatar", avatar);
            json.put("email", email);
//            json.put("modelList", userFromDB.getModelList());
//            json.put("modelItemList", userFromDB.get());

//            json.put("unreadApply", unreadApply);
//            json.put("unreadReply", unreadReply);
//            json.put("joinedProjects", userFromDB.getJoinedProjects());
//            json.put("createdProjects", userFromDB.getCreatedProjects());
            json.put("token", "Bearer" + " " + jwtToken);

            return json;
        }else{
            throw new MyException(ResultEnum.USER_PASSWORD_NOT_MATCH);
        }


//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        String IP = Utils.getIpAddr(request);
//        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        JSONObject userJson= userServiceServie.login(email, password, IP);
//        if(!userRepository.findByEmail(email).isPresent() && userJson != null){
//            // 本地没有记录，远程数据库有记录
//            // 字段插入数据库
//            User addUserDTO = new User();
//            addUserDTO.setEmail(email);
//            addUserDTO.setPassword(password);
//            addUserDTO.setUserServerId(((JSONObject)userJson.get("data")).getStr("userId"));
//            addUserDTO.setName(((JSONObject)userJson.get("data")).getStr("name"));
//            addUserDTO.setJoinedProjects(new ArrayList<String>());
//            addUserDTO.setCreatedProjects(new ArrayList<String>());
//            addUserDTO.setForkedProjects(new ArrayList<String>());
//            System.out.println(addUserDTO.toString());
//            userRepository.insert(addUserDTO);
//        }else if (!userRepository.findByEmail(email).isPresent() && userJson == null) {
//            //本地没有记录，远程没有记录
//            throw new MyException(ResultEnum.NO_OBJECT);
//        }else if (userRepository.findByEmail(email).isPresent() && userJson != null) {
//            // 本地有记录，远程数据库有记录
//            User user = userRepository.findByEmail(email).orElseThrow(MyException::noObject);
//            if(!user.getUserServerId().equals(((JSONObject)userJson.get("data")).getStr("userId"))) {user.setUserServerId(((JSONObject)userJson.get("data")).getStr("userId"));}
//            if(!user.getName().equals(((JSONObject)userJson.get("data")).getStr("name"))) {user.setName(((JSONObject)userJson.get("data")).getStr("name"));}
//            if(!user.getPassword().equals(password)) {user.setPassword(password);}
//            userRepository.save(user);
//        }
//        //验证成功，将数据交给前端
//        User userFromDB = userRepository.findByEmail(email).orElseThrow(MyException::noObject);
//        JSONObject temp = (JSONObject) userJson.get("data");
//        int unreadApply = noticeService.getNumForApply(temp.getStr("userId"));
//        int unreadReply = noticeService.getNumForReply(temp.getStr("userId"));
//        String avatar = temp.getStr("avatar");
//        if(avatar == null) { avatar = ""; };
//        String jwtToken = JwtUtils.generateToken(temp.getStr("userServerId"), temp.getStr("name"), password);
//        JSONObject json = new JSONObject();
//        json.put("name", temp.getStr("name"));
//        json.put("userId", temp.getStr("userId"));
//        json.put("avatar", avatar);
//        json.put("email", email);
//        json.put("unreadApply", unreadApply);
//        json.put("unreadReply", unreadReply);
//        json.put("joinedProjects", userFromDB.getJoinedProjects());
//        json.put("createdProjects", userFromDB.getCreatedProjects());
//        json.put("token", "Bearer" + " " + jwtToken);
//        return json;
    }






//    public JSONObject updateByUserId(String userId, UpdateUserDTO update, String email, String password) throws IllegalAccessException {
//        User userFromDB = userRepository.findById(userId).orElseThrow(MyException::noObject);
//        update.updateTo(userFromDB);
//        userRepository.save(userFromDB);
//        return userServiceServie.updateUserinfo(email, password, update);
//    }

    public User updateJoinedProjects(String userId, String update) {
        User userFromDB = userRepository.findById(userId).orElseThrow(MyException::noObject);
        userFromDB.getJoinedProjects().add(update);
//        update.updateTo(userFromDB);
        return userRepository.save(userFromDB);
    }

    public User updateCreatedProjects(String userId, String update) {
        User userFromDB = userRepository.findById(userId).orElseThrow(MyException::noObject);
        userFromDB.getCreatedProjects().add(update);
//        update.updateTo(userFromDB);
        return userRepository.save(userFromDB);
    }

    public User updateFolkedProjects(String userId, String update) {
        User userFromDB = userRepository.findById(userId).orElseThrow(MyException::noObject);
        userFromDB.getForkedProjects().add(update);
//        update.updateTo(userFromDB);
        return userRepository.save(userFromDB);
    }



    /*
    * 目的：检验登录，登录成功时将数据传送给前端
    * 1.检验本地和用户服务器有无注册
    * 2.没有注册抛出throw new MyException(ResultEnum.NO_OBJECT);
    * 3.注册了将数据传送给前端
    * */


    //发送邮箱验证码
    public JSONObject sendCodeEmail(String email) {
        JSONObject jsonObj = userServiceServie.getIdentifyinCode(email);
        if((int) jsonObj.get("code") == 0) {
            return jsonObj;
        }else {
            throw new MyException(ResultEnum.NO_OBJECT);
        }
    }

    /*
    * 忘记密码，通过邮箱修改密码
    * 1.修改用户服务器的密码
    * 2.修改本地数据库的密码
    * 3.本地有记录用户服务器一定有记录，用户服务器有记录本地不一定有记录，注意做判断处理
    * */
    public User forgetPassword(String email, String password, String code) {
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        JSONObject jsonObj = userServiceServie.changePWDbyEmail(email, code, password);
        if((int) jsonObj.get("code") == 0) {
            //用户服务器修改成功时
            if(userRepository.findByEmail(email).isPresent()) {
                //本地有记录，同时修改本地记录
                User userFromDB = userRepository.findByEmail(email).orElseThrow(MyException::noObject);
                userFromDB.setPassword(password);
                return userRepository.save(userFromDB);
            }else {
                //本地无记录，插入本地记录
                User user = ((JSONObject) jsonObj.get("data")).toBean(User.class);
                user.setPassword(password);
                user.setUserServerId(((JSONObject) jsonObj.get("data")).getStr("userId"));
                user.setJoinedProjects(new ArrayList<String>());
                user.setForkedProjects(new ArrayList<String>());
                user.setCreatedProjects(new ArrayList<String>());
                return userRepository.insert(user);
            }
        }else {
            throw new MyException(ResultEnum.NO_OBJECT);
        }
    }

    //修改密码
    public User updatePassword(String oldPWD, String newPWD, String email) {
        JSONObject jsonObj = userServiceServie.changePassword(oldPWD, newPWD, email);
        if(((int) jsonObj.get("code")) == 0) {
            User userFromDB = userRepository.findByEmail(email).orElseThrow(MyException::noObject);
            userFromDB.setPassword(newPWD);
            return userRepository.save(userFromDB);
        }else {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
    }

    //通过远程服务器获取用户的基本信息
    public JSONObject getUserinfoByUserService(String email, String password) {
        return userServiceServie.getUserinfo(email, password);
    }

        //JUST GET THE PROJECTS INFO
    public JSONObject getUserProjectInfo(String userId) {
        User userFromDB = userRepository.findById(userId).orElseThrow(MyException::noObject);
        return filterUserInfoProjects(userFromDB);
    }

    public JSONObject getUserInfoById(String userId) {

        User userFromDB = userRepository.findById(userId).orElseThrow(MyException::noObject);
        JSONObject obj = new JSONObject();
        JSONObject userNew = new JSONObject();
        if(userFromDB.getModelList()!=null) {
            JSONObject models = Optional.ofNullable(userFromDB)
                    .map(x -> x.getModelList())
                    .map(x -> {

                        List<String> modelIdList = x;
                        if (modelIdList != null) {
                            obj.put("modelList", modelRepository.findAllById(modelIdList));
                        } else {
                            obj.put("modelList", null);

                        }

                        return obj;
                    })
                    .orElseGet(null);
//            userNew.put("modelItemList",obj);
        }
        userNew.put("name",userFromDB.getName());
//        userNew.put("modelItemList",obj);
        userNew.put("modelList",userFromDB.getModelList());
        userNew.put("createdProjects",userFromDB.getCreatedProjects());
//        user.put("modelItemList", obj);
//        scenarioNew.put("instanceObjects", objInstance);


        return userNew;
    }

    public User update(User user){
        return userRepository.save(user);
    }

    public User star(String userId, String projectId) {
        User user = userRepository.findById(userId).orElseThrow(MyException::noObject);
        List<String> temp = user.getStaredProjects();
        if(temp == null) {
            List<String> list = new ArrayList<>();
            list.add(projectId);
            user.setStaredProjects(list);
        } else {
            user.getStaredProjects().add(projectId);
        }
        return userRepository.save(user);
    }

    public User unStar(String userId, String projectId) {
        User user = userRepository.findById(userId).orElseThrow(MyException::noObject);
        List<String> temp = user.getStaredProjects();
        int flag = 0;
        for(String str : temp) {
            if(str.equals(projectId)) {
                break;
            }
            flag++;
        }
        if(flag != temp.size()) {
            user.getStaredProjects().remove(flag);
        }
        return userRepository.save(user);
    }

    public int isSartedProject(String userId, String projectId) {
        User user = userRepository.findById(userId).orElseThrow(MyException::noObject);
        List<String> temp = user.getStaredProjects();
        if(temp == null) {
            return 0;
        } else {
            for(String str : temp) {
                if(str.equals(projectId)) {
                    return 1;
                }
            }
        }
        return 0;
    }

    //获取本地和远程数据库的用户信息
    public JSONObject getUserInfoOfLocalhostAndRemote(String userId) {
        User user = userRepository.findById(userId).orElseThrow(MyException::noObject);
        String password = user.getPassword();
        String email = user.getEmail();
        JSONObject remoteUser = getUserinfoByUserService(email, password);
        JSONObject result = new JSONObject();
        result.put("localhost", user);
        result.put("remote", remoteUser);
        return result;
    }

    public Map<String, Object> getUserInfoByUserId(String userId) {
        User user = userRepository.findById(userId).orElseThrow(MyException::noObject);
        Map<String, Object> result = new HashMap<>();
        result.put("name", user.getName());
        return result;
    }

    /**
    * @Description:查询用户创建和加入的项目
    * @Author: Yiming
    * @Date: 2022/1/10
    */
    public JSONArray getUserProjects(String userId) {
        User user = userRepository.findById(userId).orElseThrow(MyException::noObject);
        JSONArray result = new JSONArray();
        if( user.getCreatedProjects()!=null){
            for(String projectId : user.getCreatedProjects()) {
                Project temp = projectRepository.findById(projectId).orElseThrow(MyException::noObject);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userName", user.getName());
                jsonObject.put("picture", temp.getPicture());
                jsonObject.put("projectName", temp.getName());
                jsonObject.put("projectId", temp.getId());
                result.add(jsonObject);
            }
        }
        if( user.getJoinedProjects()!=null){
            for(String projectId : user.getJoinedProjects()) {
                Project temp = projectRepository.findById(projectId).orElseThrow(MyException::noObject);
                JSONObject jsonObject = new JSONObject();
                String userName = userRepository.findById(temp.getCreatorId()).orElseThrow(MyException::noObject).getName();
                jsonObject.put("userName", userName);
                jsonObject.put("picture", temp.getPicture());
                jsonObject.put("projectName", temp.getName());
                jsonObject.put("projectId", temp.getId());
                result.add(jsonObject);
            }
        }

        return result;
    }

    public Object updateModelList(String userId, List<String> update) {
        User userFromDB = userRepository.findById(userId).orElseThrow(MyException::noObject);

        userFromDB.setModelList(update);
        return userRepository.save(userFromDB);
    }
}
