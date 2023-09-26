package edu.njnu.opengms.r2.utils;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import edu.njnu.opengms.r2.domain.model.support.ModelBehavior;
import edu.njnu.opengms.r2.domain.model.support.ModelEvent;
import edu.njnu.opengms.r2.domain.model.support.Parameter;
import edu.njnu.opengms.r2.domain.model.support.State;
import edu.njnu.opengms.r2.domain.project.Project;
import edu.njnu.opengms.r2.domain.user.User;
import org.springframework.data.domain.Page;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class Utils {
    public static JSONObject convertMdl(JSONObject data){
        if (!data.containsKey("mdlJson")) {
            return null;
        }

//        String modelId= data.getJSONObject("mdlJson");

        JSONObject jsonObject = data.getJSONObject("mdlJson");
        JSONObject result = new JSONObject();
        result.put("name", data.getStr("name"));
        result.put("md5", data.getStr("md5"));
        result.put("description", data.getStr("overview"));
        result.put("behavior", new JSONObject());
//        result.put("behavior", new JSONArray());
//        result.put("states",new JSONArray());

        JSONArray states = new JSONArray();
        JSONArray jsonStates = jsonObject.getJSONArray("ModelClass").getJSONObject(0).getJSONArray("Behavior").getJSONObject(0).getJSONArray("StateGroup").getJSONObject(0).getJSONArray("States").getJSONObject(0).getJSONArray("State");
            JSONArray datasetItem = jsonObject.getJSONArray("ModelClass").getJSONObject(0).getJSONArray("Behavior").getJSONObject(0).getJSONArray("RelatedDatasets").getJSONObject(0).getJSONArray("DatasetItem");

            for (int j = 0; j < jsonStates.size(); j++) {
                JSONObject stateJson = new JSONObject();
                stateJson.put("stateId", jsonStates.getJSONObject(j).getStr("id"));
                stateJson.put("name", jsonStates.getJSONObject(j).getStr("name"));
                stateJson.put("description", jsonStates.getJSONObject(j).getStr("description"));
                stateJson.put("inputs", new JSONArray());
                stateJson.put("outputs", new JSONArray());
                stateJson.put("parameters", new JSONArray());

                for (int i = 0; i < jsonStates.getJSONObject(j).getJSONArray("Event").size(); i++) {
                    JSONObject temp = new JSONObject();
                    JSONObject event = (JSONObject) jsonStates.getJSONObject(j).getJSONArray("Event").get(i);

                    temp.put("eventId", UUID.randomUUID().toString());
                    temp.put("name", event.getStr("name"));
                    temp.put("description", event.getStr("description"));
//                    temp.put("optional", event.getStr("optional"));
                    if (!event.containsKey("optional")) {
                        temp.put("isOptional", true);
                    }else{
                        if (event.getStr("optional").equals("False") || event.getStr("optional").equals(false)) {
                            temp.put("isOptional", false);
                        }
                        if (event.getStr("optional").equals("True") || event.getStr("optional").equals(true)) {
                            temp.put("isOptional", true);
                        }
                    }


                    if (event.containsKey("ResponseParameter")) {
                        String datasetReference = ((JSONObject) event.getJSONArray("ResponseParameter").get(0)).getStr("datasetReference");
                        for (int a = 0; a < datasetItem.size(); a++) {
                            JSONObject item = (JSONObject) datasetItem.get(a);
                            //新增是否为参数or文件
                            item = Utils.judgeIsParam2(item);
                            if (item.getStr("name").equals(datasetReference)) {
                                event.put("datasetItem", item);
                            }
                        }
                    }

                    if (event.containsKey("DispatchParameter")) {
                        String datasetReference = ((JSONObject) event.getJSONArray("DispatchParameter").get(0)).getStr("datasetReference");
                        for (int a = 0; a < datasetItem.size(); a++) {
                            JSONObject item = (JSONObject) datasetItem.get(a);
                            if (item.getStr("name").equals(datasetReference)) {
                                event.put("datasetItem", item);
                            }
                        }
                    }

                    if (event.containsKey("ControlParameter")) {
                        String datasetReference = ((JSONObject) event.getJSONArray("ControlParameter").get(0)).getStr("datasetReference");
                        for (int a = 0; a < datasetItem.size(); a++) {
                            JSONObject item = (JSONObject) datasetItem.get(a);
                            //新增是否为参数or文件
                            item = Utils.judgeIsParam2(item);
                            if (item.getStr("name").equals(datasetReference)) {
                                event.put("datasetItem", item);
                            }
                        }
                    }


                    if (event.getStr("type").equals("response")) {
                        if (event.containsKey("datasetItem") && event.getJSONObject("datasetItem").getStr("isParams").equals("true")) {
                            temp.put("tooltip", event.getJSONObject("datasetItem").getStr("datasetReference"));
                            temp.put("datasetItem", event.get("datasetItem"));
                            stateJson.getJSONArray("parameters").add(temp);//judge input
                        } else {
                            temp.put("datasetItem", event.get("datasetItem"));
                            stateJson.getJSONArray("inputs").add(temp);//judge input
                        }
                    }

                    if (event.getStr("type").equals("noresponse")) {
                        temp.put("datasetItem", event.get("datasetItem"));
                        stateJson.getJSONArray("outputs").add(temp);//judge parameter
                    }
                }

                states.add(stateJson);
            }
            result.put("behavior",states);

//            result.getJSONObject("behavior").put(states);
            return result;


    }

    public static JSONObject convertMdlNew(JSONObject data){
        JSONObject jsonObject = data.getJSONObject("mdlJson");

        JSONObject result = new JSONObject();
//        Model modelResult = new ;
        result.put("name", data.getStr("name"));
        result.put("md5",data.getStr("md5"));
        result.put("description",data.getStr("overview"));
        result.put("behavior", new ModelBehavior());


        ModelBehavior behavior = new ModelBehavior();
        List<State> states = new ArrayList<>();
//        stateJson.setStates();


//        result.put("states",new JSONArray());

        JSONArray jsonStates = jsonObject.getJSONArray("ModelClass").getJSONObject(0).getJSONArray("Behavior").getJSONObject(0).getJSONArray("StateGroup").getJSONObject(0).getJSONArray("States").getJSONObject(0).getJSONArray("State");
        JSONArray datasetItem = jsonObject.getJSONArray("ModelClass").getJSONObject(0).getJSONArray("Behavior").getJSONObject(0).getJSONArray("RelatedDatasets").getJSONObject(0).getJSONArray("DatasetItem");
        for (int j = 0; j < jsonStates.size(); j++) {
            State stateJson = new State();
            stateJson.setStateId( jsonStates.getJSONObject(j).getStr("id"));
            stateJson.setName( jsonStates.getJSONObject(j).getStr("name"));
            stateJson.setDescription( jsonStates.getJSONObject(j).getStr("description"));
//            stateJson.setInputs();


            List<ModelEvent> inputs = new ArrayList<>();
            List<ModelEvent> outputs = new ArrayList<>();
            List<Parameter> parameters = new ArrayList<>();

            for (int i = 0; i < jsonStates.getJSONObject(j).getJSONArray("Event").size(); i++) {
                ModelEvent inputEvent = new ModelEvent();
                ModelEvent outputEvent = new ModelEvent();
                Parameter parameterEvent = new Parameter();


                JSONObject temp = new JSONObject();
                JSONObject event = (JSONObject) jsonStates.getJSONObject(j).getJSONArray("Event").get(i);

                temp.put("eventId", UUID.randomUUID().toString());
                temp.put("name", event.getStr("name"));
                temp.put("description", event.getStr("description"));
                temp.put("optional", event.getStr("optional"));

                if (event.getStr("optional").equals("False") || event.getStr("optional").equals(false)) {
                    temp.put("optional", false);
                }
                if (event.getStr("optional").equals("True") || event.getStr("optional").equals(true)) {
                    temp.put("optional", true);
                }



                if (event.containsKey("ResponseParameter")) {
                    String datasetReference = ((JSONObject) event.getJSONArray("ResponseParameter").get(0)).getStr("datasetReference");
                    for (int a = 0; a < datasetItem.size(); a++) {
                        JSONObject item = (JSONObject) datasetItem.get(a);
                        //新增是否为参数or文件
                        item = Utils.judgeIsParam(item);
                        if (item.getStr("name").equals(datasetReference)) {
                            event.put("datasetItem", item);
                        }
                    }
                }

                if (event.containsKey("DispatchParameter")) {
                    String datasetReference = ((JSONObject) event.getJSONArray("DispatchParameter").get(0)).getStr("datasetReference");
                    for (int a = 0; a < datasetItem.size(); a++) {
                        JSONObject item = (JSONObject) datasetItem.get(a);
                        if (item.getStr("name").equals(datasetReference)) {
                            event.put("datasetItem", item);
                        }
                    }
                }

                if (event.containsKey("ControlParameter")) {
                    String datasetReference = ((JSONObject) event.getJSONArray("ControlParameter").get(0)).getStr("datasetReference");
                    for (int a = 0; a < datasetItem.size(); a++) {
                        JSONObject item = (JSONObject) datasetItem.get(a);
                        if (item.getStr("name").equals(datasetReference)) {
                            event.put("datasetItem", item);
                        }
                    }
                }


                if (event.getStr("type").equals("response")) {
                    if(event.containsKey("datasetItem") && event.getJSONObject("datasetItem").getStr("isParams").equals("true")){
                        parameterEvent.setEventId(UUID.randomUUID().toString());
                        parameterEvent.setDescription(event.getStr("description"));
                        parameterEvent.setName(event.getStr("name"));
                        parameterEvent.setIsOptional(temp.getBool("optional"));
                        parameterEvent.setTooltip(event.getJSONObject("datasetItem").getStr("datasetReference"));

                    }else{
                        inputEvent.setEventId(UUID.randomUUID().toString());
                        inputEvent.setDescription(event.getStr("description"));
                        inputEvent.setName(event.getStr("name"));
                        inputEvent.setIsOptional(temp.getBool("optional"));

//                        inputEvent.setEventId();
                    }
                }

                if (event.getStr("type").equals("noresponse")) {
                    outputEvent.setEventId(UUID.randomUUID().toString());
                    outputEvent.setDescription(event.getStr("description"));
                    outputEvent.setName(event.getStr("name"));
                    outputEvent.setIsOptional(temp.getBool("optional"));
                }

                inputs.add(inputEvent);
                outputs.add(outputEvent);
                parameters.add(parameterEvent);
            }

            stateJson.setInputs(inputs);
            stateJson.setOutputs(outputs);
            stateJson.setParameters(parameters);
            states.add(stateJson);

        }
        behavior.setStates(states);

        return result;

    }


//    public static JSONObject judgeIsParam(JSONObject datasetItem){
//        boolean flag = datasetItem.containsKey("UdxDeclaration");
//        if(flag) {
//            datasetItem.put("isParams", "true");
//        } else {
//            datasetItem.put("isParams", "false");
//        }
//        return datasetItem;
//    }
    public static JSONObject judgeIsParam(JSONObject datasetItem){
        boolean flag = datasetItem.containsKey("UdxDeclaration");
        JSONArray UdxDeclarationNew=new JSONArray();
        JSONObject tempUDX = new JSONObject();
        if(flag) {
            JSONObject test = (JSONObject) datasetItem.getJSONArray("UdxDeclaration").get(0);
            if( test!=null && test.get("UdxNode")!=null&& test.get("UdxNode").toString()!="" ){
                JSONObject test2 = (JSONObject) test.getJSONArray("UdxNode").get(0);
                if(test2!=null && test2.get("UdxNode")!=null&& test2.get("UdxNode").toString()!="" ){
                    if(test2.containsKey("type")){
                        tempUDX.put("name",test2.getStr("name"));
                        tempUDX.put("type",test2.getStr("type"));
                        tempUDX.put("description",test2.getStr("description"));
                        if(test2.getStr("type").equals("DTKT_ANY")){
                            datasetItem.put("isParams", "false");
                            datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);
                        } else {
                            datasetItem.put("isParams", "true");
                            datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);
                        }
                    }else{
                        JSONObject test3 = (JSONObject) test2.getJSONArray("UdxNode").get(0);
                        if (!test3.containsKey("type")){
                            datasetItem.put("isParams", "false");
                        }
                        else if(test3.getStr("type").equals("DTKT_ANY")){
                            datasetItem.put("isParams", "false");
                            tempUDX.put("name",test3.getStr("name"));
                            tempUDX.put("type",test3.getStr("type"));
                            tempUDX.put("description",test3.getStr("description"));
                            datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);
                        }
                        else{
                            datasetItem.put("isParams", "true");
                            tempUDX.put("name",test3.getStr("name"));
                            tempUDX.put("type",test3.getStr("type"));
                            tempUDX.put("description",test3.getStr("description"));
                            datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);
                        }
                    }
                }else {
                    datasetItem.put("isParams", "false");
                    datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);
                }

            }else {
                datasetItem.put("isParams", "false");
                datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);
            }
        } else {
            datasetItem.put("isParams", "false");
            datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);

        }
        return datasetItem;
    }

    public static JSONObject judgeIsParam3(JSONObject datasetItem){
        boolean flag = datasetItem.containsKey("UdxDeclaration");
        JSONArray UdxDeclarationNew=new JSONArray();
        JSONObject tempUDX = new JSONObject();
        if(flag) {

            JSONObject test = (JSONObject) datasetItem.getJSONArray("UdxDeclaration").get(0);

            if( test!=null && test.get("UdxNode")!=null&& test.get("UdxNode").toString()!="" ){
                JSONObject test2 = (JSONObject) test.getJSONArray("UdxNode").get(0);
                if(test2!=null && test2.get("UdxNode")!=null&& test2.get("UdxNode").toString()!="" ){
                    if(test2.containsKey("type")){
                        tempUDX.put("name",test2.getStr("name"));
                        tempUDX.put("type",test2.getStr("type"));
                        tempUDX.put("description",test2.getStr("description"));
                        if(test2.getStr("type").equals("DTKT_ANY")){
                            datasetItem.put("isParams", "false");
                            datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);
                        } else {
                            datasetItem.put("isParams", "true");
                            datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);
                        }
                    }else{
                        JSONObject test3 = (JSONObject) test2.getJSONArray("UdxNode").get(0);
                        if (!test3.containsKey("type")){
                            datasetItem.put("isParams", "false");
                        }
                        else if(test3.getStr("type").equals("DTKT_ANY")){
                            datasetItem.put("isParams", "false");
                            tempUDX.put("name",test3.getStr("name"));
                            tempUDX.put("type",test3.getStr("type"));
                            tempUDX.put("description",test3.getStr("description"));
                            datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);
                        }
                        else{
                            datasetItem.put("isParams", "true");
                            tempUDX.put("name",test3.getStr("name"));
                            tempUDX.put("type",test3.getStr("type"));
                            tempUDX.put("description",test3.getStr("description"));
                            datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);
                        }
                    }
                }else {
                    datasetItem.put("isParams", "false");
                    datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);
                }

            }else {
                datasetItem.put("isParams", "false");
                datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);
            }
        } else {
            datasetItem.put("isParams", "false");
            datasetItem.put("UdxDeclarationNew", UdxDeclarationNew);

        }
        return datasetItem;
    }

    public static JSONObject judgeIsParam2(JSONObject datasetItem){
        boolean flag = datasetItem.containsKey("UdxDeclaration");
        JSONArray UdxDeclarationNew=new JSONArray();
        JSONObject tempUDX = new JSONObject();
        if(flag) {
            JSONArray testArray = datasetItem.getJSONArray("UdxDeclaration");
            if (testArray != null && testArray.toString() != "") {
                for (int i = 0; i < testArray.size(); i++) {
                    JSONObject test2Arr = (JSONObject) testArray.get(i);
                    if (test2Arr != null && test2Arr.toString() != "") {
                        if (test2Arr.containsKey("type") ){
                            tempUDX.put("name", test2Arr.getStr("name"));
                            tempUDX.put("type", test2Arr.getStr("type"));
                            tempUDX.put("description", test2Arr.getStr("description"));
                            if (test2Arr.getStr("type").equals("DTKT_ANY")) {
                                datasetItem.put("isParams", "false");
                            } else {
                                datasetItem.put("isParams", "true");
                            }
                            UdxDeclarationNew.add(tempUDX);
                        }
                        else{
                            if (test2Arr.containsKey("UdxNode") && test2Arr.get("UdxNode") != null && test2Arr.get("UdxNode").toString() != "") {
                                    for (int j = 0; j < test2Arr.getJSONArray("UdxNode").size(); j++) {
                                        JSONObject test3Arr = (JSONObject) test2Arr.getJSONArray("UdxNode").get(j);
                                        if (test3Arr != null && test3Arr.toString() != "") {
                                            if (test3Arr.containsKey("type") ){
                                                tempUDX.put("name", test3Arr.getStr("name"));
                                                tempUDX.put("type", test3Arr.getStr("type"));
                                                tempUDX.put("description", test3Arr.getStr("description"));
                                                if (test3Arr.getStr("type").equals("DTKT_ANY")) {
                                                    datasetItem.put("isParams", "false");
                                                } else {
                                                    datasetItem.put("isParams", "true");
                                                }
                                                UdxDeclarationNew.add(tempUDX);
                                            } else{
                                                if (test3Arr.containsKey("UdxNode") && test3Arr.get("UdxNode") != null && test3Arr.get("UdxNode").toString() != "") {
                                                        for (int k = 0; k < test3Arr.getJSONArray("UdxNode").size(); k++) {
                                                            JSONObject test4Arr = (JSONObject) test3Arr.getJSONArray("UdxNode").get(k);
                                                            if (test4Arr != null && test4Arr.toString() != "") {
                                                                if (test4Arr.containsKey("type") ){
                                                                    tempUDX.put("name", test4Arr.getStr("name"));
                                                                    tempUDX.put("type", test4Arr.getStr("type"));
                                                                    tempUDX.put("description", test4Arr.getStr("description"));
                                                                    if (test4Arr.getStr("type").equals("DTKT_ANY")) {
                                                                        datasetItem.put("isParams", "false");
                                                                    } else {
                                                                        datasetItem.put("isParams", "true");
                                                                    }
                                                                    UdxDeclarationNew.add(tempUDX);
                                                                } else {
                                                                    if (test4Arr.containsKey("UdxNode") && test4Arr.get("UdxNode") != null && test4Arr.get("UdxNode").toString() != "") {
                                                                        for (int s = 0; s < test4Arr.getJSONArray("UdxNode").size(); s++) {
                                                                            JSONObject test5Arr = (JSONObject) test4Arr.getJSONArray("UdxNode").get(k);
                                                                            if (test4Arr != null && test4Arr.toString() != "") {
                                                                                if (test5Arr.containsKey("type")) {
                                                                                    tempUDX.put("name", test5Arr.getStr("name"));
                                                                                    tempUDX.put("type", test5Arr.getStr("type"));
                                                                                    tempUDX.put("description", test5Arr.getStr("description"));
                                                                                    if (test3Arr.getStr("type").equals("DTKT_ANY")) {
                                                                                        datasetItem.put("isParams", "false");
                                                                                    } else {
                                                                                        datasetItem.put("isParams", "true");
                                                                                    }
                                                                                    UdxDeclarationNew.add(tempUDX);
                                                                                }else{
                                                                                    datasetItem.put("isParams", "false");
                                                                                }
                                                                            }else{
                                                                                datasetItem.put("isParams", "false");
                                                                            }
                                                                        }
                                                                    }
                                                                    else{
                                                                        datasetItem.put("isParams", "false");
                                                                    }
                                                                }
                                                            }else{
                                                                datasetItem.put("isParams", "false");
                                                            }
                                                        }

                                                }else{
                                                    datasetItem.put("isParams", "false");
                                                }
                                            }

                                        }else{
                                            datasetItem.put("isParams", "false");
                                        }
                                    }

                            }else{
                                datasetItem.put("isParams", "false");
                            }
                        }

                    }else{
                        datasetItem.put("isParams", "false");
                    }
                }
            }else{
                datasetItem.put("isParams", "false");
            }

        } else{
            datasetItem.put("isParams", "false");
        }
        datasetItem.put("UdxDeclarationNew",UdxDeclarationNew);
        return datasetItem;
    }







        public static JSONArray filterUserInfo(List<User> users) {
        JSONArray userList = new JSONArray();
        for (User user : users) {
            JSONObject jsonObject = new JSONObject();
//            jsonObject.put("value", user.getName());
//            jsonObject.put("userId", user.getUserId());
            jsonObject.put("email", user.getEmail());
            userList.add(jsonObject);
        }
        return userList;
    }

    public static JSONObject filterUserInfoProjects(User user) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("createdProjects", user.getCreatedProjects());
        jsonObject.put("joinedProjects", user.getJoinedProjects());
        return jsonObject;
    }

    public static JSONArray addProjectRole(Page<Project> projectList, User user) {
        JSONArray projectList1 = new JSONArray();

        for (Project project : projectList.getContent()) {
            JSONObject newProject = new JSONObject();
            newProject.put("creatorId", project.getCreatorId());
            newProject.put("memberList",project.getMemberList());
            newProject.put("id", project.getId());
            newProject.put("name", project.getName());
            newProject.put("description", project.getDescription());
            newProject.put("picture", project.getPicture());

            newProject.put("userRole", "visitor");

            for (String create : user.getCreatedProjects()) {
                if (create.equals(project.getId())) {
                    newProject.put("userRole", "builder");
                }
            }
            for (String join : user.getJoinedProjects()) {
                if (join.equals(project.getId())) {
                    newProject.put("userRole", "rebuilder");
                }
            }

            projectList1.add(newProject);
        }
        return projectList1;
    }

    //获取请求 ip 地址
    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress = null;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1")) {
                    // 根据网卡取本机配置的IP
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) { // "***.***.***.***".length()
                // = 15
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        // ipAddress = this.getRequest().getRemoteAddr();
        return ipAddress;
    }

    /**
    * @Description: 修改数据库IntegrateTaskInstance中的taskContent
    * @Author: Yiming
    * @Date: 2021/11/3
    */

//    public static void changeStatusOfCell(IntegrateTaskInstance integrateTaskInstance, List<Map<String, String>> completedValue, List<Map<String, String>> failedValue) {
//        List<Process> modelFailed = integrateTaskInstance.getTaskInfo().getModelActionList().getFailed();
//        List<Process> modelCompleted = integrateTaskInstance.getTaskInfo().getModelActionList().getCompleted();
//        List<Process> modelRunning = integrateTaskInstance.getTaskInfo().getModelActionList().getRunning();
//        List<Process> dataServiceFailed = integrateTaskInstance.getTaskInfo().getDataProcessingList().getFailed();
//        List<Process> dataServiceCompleted = integrateTaskInstance.getTaskInfo().getDataProcessingList().getCompleted();
//        List<Process> dataServiceRunning = integrateTaskInstance.getTaskInfo().getModelActionList().getRunning();
//        for(Map<String, String> temp : completedValue) {
//            String id = temp.keySet().toArray(new String[1])[0];
//            String value = temp.get(id);
//            String oldStr = "<mxCell id=\"" + id + "\" value=\"\"";
//            String newStr = "<mxCell id=\"" + id + "\" value=\"" + value + "\"";
//            integrateTaskInstance.setTaskContent(integrateTaskInstance.getTaskContent().replaceAll(oldStr, newStr));
//        }
//        for(Map<String, String> temp : failedValue) {
//            String id = temp.keySet().toArray(new String[1])[0];
//            String oldStr = "<mxCell id=\"" + id + "\" value=\"\"";
//            String newStr = "<mxCell id=\"" + id + "\" value=\"" + "failed" + "\"";
//            integrateTaskInstance.setTaskContent(integrateTaskInstance.getTaskContent().replaceAll(oldStr, newStr));
//        }
//        for(Process p : modelFailed) {
//            String id = p.getId();
//            String oldStr = "<mxCell id=\"" + id + "\" style=\"fontColor=#......;fillColor=#......";
//            String newStr = "<mxCell id=\"" + id + "\" style=\"fontColor=#f6f6f6;fillColor=#ce1212";
//            integrateTaskInstance.setTaskContent(integrateTaskInstance.getTaskContent().replaceAll(oldStr, newStr));
//        }
//        for(Process p : dataServiceFailed) {
//            String id = p.getId();
//            String oldStr = "<mxCell id=\"" + id + "\" style=\"fontColor=#......;fillColor=#......";
//            String newStr = "<mxCell id=\"" + id + "\" style=\"fontColor=#f6f6f6;fillColor=#ce1212";
//            integrateTaskInstance.setTaskContent(integrateTaskInstance.getTaskContent().replaceAll(oldStr, newStr));
//        }
//        for(Process p : modelCompleted) {
//            String id = p.getId();
//            String oldStr = "<mxCell id=\"" + id + "\" style=\"fontColor=#......;fillColor=#......";
//            String newStr = "<mxCell id=\"" + id + "\" style=\"fontColor=#24292E;fillColor=#67C23A";
//            integrateTaskInstance.setTaskContent(integrateTaskInstance.getTaskContent().replaceAll(oldStr, newStr));
//        }
//        for(Process p : dataServiceCompleted) {
//            String id = p.getId();
//            String oldStr = "<mxCell id=\"" + id + "\" style=\"fontColor=#......;fillColor=#......";
//            String newStr = "<mxCell id=\"" + id + "\" style=\"fontColor=#24292E;fillColor=#67C23A";
//            integrateTaskInstance.setTaskContent(integrateTaskInstance.getTaskContent().replaceAll(oldStr, newStr));
//        }
//        for(Process p : modelRunning) {
//            String id = p.getId();
//            String oldStr = "<mxCell id=\"" + id + "\" style=\"fontColor=#......;fillColor=#......";
//            String newStr = "<mxCell id=\"" + id + "\" style=\"fontColor=#f6f6f6;fillColor=#E6A23C";
//            integrateTaskInstance.setTaskContent(integrateTaskInstance.getTaskContent().replaceAll(oldStr, newStr));
//        }
//        for(Process p : dataServiceRunning) {
//            String id = p.getId();
//            String oldStr = "<mxCell id=\"" + id + "\" style=\"fontColor=#......;fillColor=#......";
//            String newStr = "<mxCell id=\"" + id + "\" style=\"fontColor=#f6f6f6;fillColor=#E6A23C";
//            integrateTaskInstance.setTaskContent(integrateTaskInstance.getTaskContent().replaceAll(oldStr, newStr));
//        }
//    }

    /**
    * @Description:对checkTask接口返回数据进行处理，获取output的（id，value）键值对
    * @Author: Yiming
    * @Date: 2021/11/3
    */

    public static List<Map<String, String>> getValueList(JSONObject data, String type) {
        List<Map<String, String>> list = new ArrayList<>();
        if(type.equals("completed")) {
            JSONArray modelCompleted = data.getJSONObject("taskInfo").getJSONObject("modelActionList").getJSONArray("completed");
            for(int i = 0;i < modelCompleted.size();i++) {
                JSONArray outputs = modelCompleted.get(i, JSONObject.class).getJSONObject("outputData").getJSONArray("outputs");
                for(int j = 0;j < outputs.size();j++) {
                    Map<String, String> temp = new HashMap<>();
                    temp.put(outputs.get(j, JSONObject.class).getStr("dataId"), outputs.get(j, JSONObject.class).getJSONObject("dataContent").getStr("value"));
                    list.add(temp);
                }
            }
            JSONArray dataServiceCompleted = data.getJSONObject("taskInfo").getJSONObject("dataProcessingList").getJSONArray("completed");
            for(int i = 0;i < dataServiceCompleted.size();i++) {
                JSONArray outputs = dataServiceCompleted.get(i, JSONObject.class).getJSONObject("outputData").getJSONArray("outputs");
                for(int j = 0;j < outputs.size();j++) {
                    Map<String, String> temp = new HashMap<>();
                    temp.put(outputs.get(j, JSONObject.class).getStr("dataId"), outputs.get(j, JSONObject.class).getJSONObject("dataContent").getStr("value"));
                    list.add(temp);
                }
            }
        } else if(type.equals("failed")) {
            JSONArray modelFailed = data.getJSONObject("taskInfo").getJSONObject("modelActionList").getJSONArray("failed");
            for(int i = 0;i < modelFailed.size();i++) {
                JSONArray outputs = modelFailed.get(i, JSONObject.class).getJSONObject("outputData").getJSONArray("outputs");
                for(int j = 0;j < outputs.size();j++) {
                    Map<String, String> temp = new HashMap<>();
                    temp.put(outputs.get(j, JSONObject.class).getStr("dataId"), outputs.get(j, JSONObject.class).getJSONObject("dataContent").getStr("value"));
                    list.add(temp);
                }
            }
            JSONArray dataServiceFailed = data.getJSONObject("taskInfo").getJSONObject("dataProcessingList").getJSONArray("failed");
            for(int i = 0;i < dataServiceFailed.size();i++) {
                JSONArray outputs = dataServiceFailed.get(i, JSONObject.class).getJSONObject("outputData").getJSONArray("outputs");
                for(int j = 0;j < outputs.size();j++) {
                    Map<String, String> temp = new HashMap<>();
                    temp.put(outputs.get(j, JSONObject.class).getStr("dataId"), outputs.get(j, JSONObject.class).getJSONObject("dataContent").getStr("value"));
                    list.add(temp);
                }
            }
        }
        return list;
    }

    /**
    * @Description:文件的大小以特定格式输出
    * @Author: Yiming
    * @Date: 2021/11/23
    */
    public static String getPrintSize(long size) {
        // 如果字节数少于1024，则直接以B为单位，否则先除于1024，后3位因太少无意义
        double value = (double) size;
        if (value < 1024) {
            return String.valueOf(value) + "B";
        } else {
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        // 如果原字节数除于1024之后，少于1024，则可以直接以KB作为单位
        // 因为还没有到达要使用另一个单位的时候
        // 接下去以此类推
        if (value < 1024) {
            return String.valueOf(value) + "KB";
        } else {
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
        }
        if (value < 1024) {
            return String.valueOf(value) + "MB";
        } else {
            // 否则如果要以GB为单位的，先除于1024再作同样的处理
            value = new BigDecimal(value / 1024).setScale(2, BigDecimal.ROUND_DOWN).doubleValue();
            return String.valueOf(value) + "GB";
        }
    }




}
