package edu.njnu.opengms.r2.domain.user;


import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2020/12/4 14:28
 * @modified By：
 * @version: 1.0.0
 */

@RestController
@RequestMapping(value = "/users")
public class UserController {
    @Autowired
    UserService userService;


    //userId 均为 user--id;
    @RequestMapping(value = "", method = RequestMethod.GET)
    public JsonResult getUserInfo(@JwtTokenParser(key = "userId") String userId) {
        return ResultUtils.success(userService.getUserInfoById(userId));
    }

    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public JsonResult getUserProjectInfo(@JwtTokenParser(key = "userId") String userId) {
        return ResultUtils.success(userService.getUserProjectInfo(userId));
    }


    @RequestMapping(value = "/like/{email}", method = RequestMethod.GET)
    public JsonResult getUserInfoLike(@PathVariable String email) {
        return ResultUtils.success(userService.getUserInfoLike(email));
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public JsonResult doRegister(@RequestParam("email") String email, @RequestParam("name") String name, @RequestParam("password") String password) {
        return ResultUtils.success(userService.register(name, email, password));
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public JsonResult doLogin(@RequestParam("email") String email, @RequestParam("password") String password) {
        return ResultUtils.success(userService.login(email, password));
    }

    @RequestMapping(value = "/create", method = RequestMethod.PATCH)
    public JsonResult updateCreatedProjects(@JwtTokenParser(key = "userId") String userId, @RequestBody String update) {
        return ResultUtils.success(userService.updateCreatedProjects(userId, update));
    }


    //调用远程服务器，获取用户基本信息
    @RequestMapping(value = "/getuserinfo", method = RequestMethod.GET)
    public JsonResult getUserinfo(@JwtTokenParser(key = "email") String email, @JwtTokenParser(key = "password") String password) {
        return ResultUtils.success(userService.getUserinfoByUserService(email, password));
    }

    //获取本地和远程数据库的用户信息
    @RequestMapping(value = "/getAllInfo/{userId}", method = RequestMethod.GET)
    public JsonResult getAllUserInfo(@PathVariable String userId) {
        return ResultUtils.success(userService.getUserInfoOfLocalhostAndRemote(userId));
    }

    @RequestMapping(value = "/getUserInfoByUserId/{userId}", method = RequestMethod.GET)
    public JsonResult getUserInfoByUserId(@PathVariable String userId) {
        return ResultUtils.success(userService.getUserInfoByUserId(userId));
    }

    @RequestMapping(value = "/getUserProjects", method = RequestMethod.GET)
    public JsonResult getUserProjects(@JwtTokenParser(key = "userId") String userId) {
        return ResultUtils.success(userService.getUserProjects(userId));
    }

    //model related
    @RequestMapping(value = "/model", method = RequestMethod.PATCH)
    public JsonResult updateModelList(@JwtTokenParser(key = "userId") String userId, @RequestBody List<String> update) {
        return ResultUtils.success(userService.updateModelList(userId, update));
    }

}
