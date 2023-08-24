package edu.njnu.opengms.r2.domain.user;


import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.annotation.JwtTokenParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @RequestMapping (value = "", method = RequestMethod.GET)
    public JsonResult getUserInfo(@JwtTokenParser(key="userId") String userId)  {
        return ResultUtils.success(userService.getUserInfoById(userId));
    }

    @RequestMapping (value = "/projects", method = RequestMethod.GET)
    public JsonResult getUserProjectInfo(@JwtTokenParser(key="userId") String userId)  {
        return ResultUtils.success(userService.getUserProjectInfo(userId));
    }

    @RequestMapping (value = "/like/{email}", method = RequestMethod.GET)
    public JsonResult getUserInfoLike(@PathVariable String email)  {
        return ResultUtils.success(userService.getUserInfoLike(email));
    }

    @RequestMapping (value = "/register", method = RequestMethod.POST)
    public JsonResult doRegister(@RequestParam("email") String email,@RequestParam ("name") String name,@RequestParam ("password") String password) {
        return ResultUtils.success(userService.register(name,email, password));
    }

    @RequestMapping (value = "/login", method = RequestMethod.POST)
    public JsonResult doLogin(@RequestParam ("email") String email,@RequestParam ("password") String password) {
        return ResultUtils.success(userService.login(email, password));
    }

//    @RequestMapping (value = "", method = RequestMethod.PATCH)
//    public JsonResult update(@JwtTokenParser(key="userId") String userId, @RequestBody UpdateUserDTO update, @JwtTokenParser(key = "email") String email, @JwtTokenParser(key = "password") String password) throws IllegalAccessException {
//        return ResultUtils.success(userService.updateByUserId(userId, update, email, password));
//    }

    @RequestMapping (value = "/join", method = RequestMethod.PATCH)
    public JsonResult updateJoinedProjects( @JwtTokenParser(key="userId") String userId, @RequestBody  String update) {
        return ResultUtils.success(userService.updateJoinedProjects(userId,update));
    }
    @RequestMapping (value = "/create", method = RequestMethod.PATCH)
    public JsonResult updateCreatedProjects(@JwtTokenParser(key="userId") String userId, @RequestBody String update) {
        return ResultUtils.success(userService.updateCreatedProjects(userId,update));
    }

    @RequestMapping (value = "/folk", method = RequestMethod.PATCH)
    public JsonResult updateFolkedProjects(@JwtTokenParser(key="userId") String userId, @RequestBody String update) {
        return ResultUtils.success(userService.updateFolkedProjects(userId,update));
    }

    //忘记密码，通过邮箱修改
    @RequestMapping (value = "/{email}/{code}/{password}", method = RequestMethod.PATCH)
    public JsonResult forgetPasswordByEmail(@PathVariable String email,@PathVariable String code, @PathVariable String password) {
        return ResultUtils.success(userService.forgetPassword(email, password, code));
    }

    //修改密码
    @RequestMapping (value = "/changePWD/{oldPWD}/{newPWD}", method = RequestMethod.PATCH)
    public JsonResult updatePassword(@PathVariable String oldPWD,@PathVariable String newPWD, @JwtTokenParser(key = "email") String email) {
        return ResultUtils.success(userService.updatePassword(oldPWD, newPWD, email));
    }

    //发送邮件，获取验证码
    @RequestMapping (value = "/sendEmail/{email}", method = RequestMethod.GET)
    public JsonResult sendCodeEmail(@PathVariable String email) {
        return ResultUtils.success(userService.sendCodeEmail(email));
    }

    //调用远程服务器，获取用户基本信息
    @RequestMapping (value = "/getuserinfo", method = RequestMethod.GET)
    public JsonResult getUserinfo(@JwtTokenParser(key = "email") String email, @JwtTokenParser(key = "password") String password) {
        return ResultUtils.success(userService.getUserinfoByUserService(email, password));
    }

    //判断用户是否star了项目
    @RequestMapping(value = "/isSart/{projectId}", method = RequestMethod.GET)
    public JsonResult isStarProject(@JwtTokenParser(key = "userId") String userId, @PathVariable String projectId) {
        return ResultUtils.success(userService.isSartedProject(userId, projectId));
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
}
