package edu.njnu.opengms.r2.remote;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.enums.ResultEnum;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.user.dto.AddUserDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceServie {
    @Value("172.21.213.245:8088")
    private String userServiceIp;
    @Value("zzy")
    private String userServerCilent;
    @Value("zzy")
    private String userServerCilentPWD;

    //从用户服务器注册
    public JSONObject register(AddUserDTO add) {
        String urlStr = "http://" + userServiceIp + "/userServer/user";
        RestTemplate restTemplate = new RestTemplate();
        Map<String, Object> param = new HashMap<>();
        param.put("email", add.getEmail());
        param.put("password", add.getPassword());
        param.put("name", add.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        headers.add("Content-Type", "application/json");
        HttpEntity httpEntity = new HttpEntity(param, headers);
        try {
//            ResponseEntity<JSONObject> jsonObjectResponseEntity = restTemplate.postForEntity(urlStr, add, JSONObject.class);
            ResponseEntity<JSONObject> result = restTemplate.exchange(urlStr, HttpMethod.POST, httpEntity, JSONObject.class);
            return result.getBody();
        }catch (Exception e) {
            System.out.println(e);
            System.out.println("用户数据库注册时出现错误");
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
    }

    //从用户服务器获取token
    public JSONObject get_token(String email, String password) {
        LinkedMultiValueMap<String, String> paramMap = new LinkedMultiValueMap<>();
        paramMap.add("client_id", userServerCilent);
        paramMap.add("client_secret", userServerCilentPWD);
        paramMap.add("username", email);
        paramMap.add("password", password);
        paramMap.add("scope", "all");
        paramMap.add("grant_type", "password");
        String authUri = "http://"+ userServiceIp + "/userServer/oauth/token";
        RestTemplate restTemplate = new RestTemplate();
        try {
            JSONObject result = restTemplate.postForObject(authUri, paramMap, JSONObject.class);
            return result;
        }catch (Exception e) {
            System.out.println(e);
            System.out.println("获取token时出错了");
            return null;
        }
    }

    //从用户服务器登录
    public JSONObject login(String email, String password, String ip) {
        JSONObject token = get_token(email, password);
        if (token == null) {
            return null;
        }
        String s = (String) token.get("access_token");
        String urlStr = "http://" + userServiceIp + "/userServer/auth/login/" + ip;
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + s);
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<JSONObject> result = restTemplate.exchange(urlStr, HttpMethod.GET, httpEntity, JSONObject.class);
            return result.getBody();
        }catch (Exception e) {
            System.out.println("用户服务器登录时出错");
            System.out.println(e);
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }

    }

    //忘记密码，获取邮箱验证码
    public JSONObject getIdentifyinCode(String email) {
        // 1.获取验证码
        String getCodeUrl = "http://" + userServiceIp + "/userServer/user/resetPwd/" + email;
        RestTemplate restTemplate = new RestTemplate();
        try {
            JSONObject result = restTemplate.getForObject(getCodeUrl, JSONObject.class);
            return result;
        }catch (Exception e) {
            System.out.println("获取邮箱验证码时出错");
            System.out.println(e);
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
    }

    //用邮箱验证码更新密码
    public JSONObject changePWDbyEmail(String email, String code, String newPassWord) {

        String url = "http://" + userServiceIp + "/userServer/user/resetPwd/" + email +"/" + code + "/" + newPassWord;
        RestTemplate restTemplate = new RestTemplate();
        try {
            JSONObject result = restTemplate.getForObject(url, JSONObject.class);
            return result;
        }catch (Exception e) {
            System.out.println("通过邮箱验证码修改密码时出错");
            System.out.println(e);
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
    }

    //旧密码更新新密码
    public JSONObject changePassword(String oldPWD, String newPWD, String email) {
        JSONObject jsonObj = get_token(email, oldPWD);
        String token = "";
        try {
            token = (String) jsonObj.get("access_token");
        } catch (Exception e) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
        String url = "http://" + userServiceIp + "/userServer/auth/newPwd/" + oldPWD + "/" + newPWD;
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<Object> httpEntity = new HttpEntity<>(headers);
        try {
            ResponseEntity<JSONObject> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class);
            return result.getBody();
        }catch (Exception e) {
            System.out.println("旧密码更新新密码时出错");
            System.out.println(e);
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
    }



    //获取用户信息
    public JSONObject getUserinfo(String email, String password) {
        String url = "http://" + userServiceIp + "/userServer/auth/userBase";
        HttpHeaders headers = new HttpHeaders();
        JSONObject jsonObj = get_token(email, password);
        String token = (String) jsonObj.get("access_token");
        headers.add("Authorization", "Bearer " + token);
        HttpEntity<Object> httpEntity = new HttpEntity(headers);
        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<JSONObject> result = restTemplate.exchange(url, HttpMethod.GET, httpEntity, JSONObject.class);
            return result.getBody();
        } catch(Exception e) {
            throw new MyException(ResultEnum.REMOTE_SERVICE_ERROR);
        }
    }
}
