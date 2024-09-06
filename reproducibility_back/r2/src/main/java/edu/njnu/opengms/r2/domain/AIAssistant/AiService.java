package edu.njnu.opengms.r2.domain.AIAssistant;

import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class AiService {

    private String uploadUrl = "http://112.4.132.6:8083/data";

    private String openaiApiKey = "sb-ccc08d92c8d641e071e73c047508bce558f84178e7906c45";

    private String openaiBaseUrl = "https://api.openai-sb.com/v1/";

    private String tempDir;

    public AiService() {
        // 获取当前类文件所在的目录
        String currentDir = new File("").getAbsolutePath();
        // 定义tempDir为当前目录下的files文件夹
        this.tempDir = currentDir + File.separator + "files";
    }

    public ResponseEntity<?> modelQuery(String modelName , String collection){
        String pythonScriptPath = "E:\\code\\newer\\Reproducibility-platform-main1102\\Reproducibility-platform\\reproducibility_back\\r2\\src\\main\\java\\edu\\njnu\\opengms\\r2\\domain\\AIAssistant\\queryItemFromChroma.py";
        try {
            // 构建ProcessBuilder对象，设置脚本路径和参数
            ProcessBuilder processBuilder = new ProcessBuilder("python", pythonScriptPath, modelName, collection);
            Process process = processBuilder.start();

            // 读取脚本的输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            StringBuilder output = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                output.append(line).append("\n");
            }

            int exitCode = process.waitFor();
            if (exitCode == 0) {
                // 脚本执行成功，返回输出结果
                return ResponseEntity.ok(output);
            } else {
                // 脚本执行失败，返回错误信息
                return ResponseEntity.status(500).body("Script execution failed with exit code " + exitCode);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("An error occurred: " + e.getMessage());
        }
    }


    public String parsePdf(String filePath) throws IOException {
        String apiUrl = "https://api.doc2x.noedgeai.com/api/platform/pdf";
        String apiKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVc2VySUQiOjMwMDY1LCJVc2VyUm9sZSI6ImN1c3RvbWVyIiwiZXhwIjoxNzIxNDY3OTU5LCJpYXQiOjE3MjEyMDg3NTl9.xpRpy99I75eKGvO57M86wowd7ZXOuIB_i8_SmHQB1UU";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + apiKey);
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        FileSystemResource fileResource = new FileSystemResource(filePath);

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("file", fileResource);
        formData.add("ocr", "true");
        formData.add("straming", "false");

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(apiUrl, requestEntity, String.class);

        removeCallback(filePath);

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            return responseEntity.getBody();
        } else {
            throw new IOException("Failed to parse PDF: " + responseEntity.getStatusCode());
        }
    }



    public Map<String, String> uploadSinglePaper(MultipartFile file) throws IOException {
        String filePath = saveTempFile(file);
        String newFileURL = URLuploadfile(filePath, false , "null");

        String nowFilePath = url2file(newFileURL, "pdf");
        String pdfContent = parsePdf(nowFilePath) ;

        JSONArray pagesArray = null;

        if (pdfContent != null) {
            String[] dataArray = pdfContent.split("data: \\{");
            for (int i = 0; i < dataArray.length; i++) {
                if (!dataArray[i].isEmpty()) {
                    dataArray[i] = "{" + dataArray[i];
                }
            }
            try{
                for (String dataBlock : dataArray) {
                    if (!dataBlock.trim().isEmpty()) {
                        // 将数据块解析为JSONObject
                        JSONObject jsonObject = new JSONObject(dataBlock.trim());
                        // 获取status字段
                        String status = jsonObject.getString("status");
                        // 检查status是否为"success"
                        if ("success".equals(status)) {
                            // 获取data字段
                            JSONObject dataObject = jsonObject.getJSONObject("data");
                            // 获取pages字段w
                            pagesArray = dataObject.getJSONArray("pages");
                        }
                    }
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
            }

            Map<String, String> allInformation = new HashMap<>();
            allInformation.put("link" , newFileURL);
            allInformation.put("content" , pdfContent);
            allInformation.put("pages" , String.valueOf(pagesArray));



            // Send response
            return allInformation;
        } else {
            System.out.println(new ResponseEntity<>("Failed to parse PDF file", HttpStatus.BAD_REQUEST));
            return null;
        }
    }


    public String URLuploadfile(String url, boolean isURL, String type) throws IOException {
        String filePath = url;

        if (isURL) {
            try {
                filePath = url2file(url, type);
            } catch (IOException e) {
                throw new IOException("Error downloading file from URL: " + e.getMessage());
            }
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        FileSystemResource fileResource = new FileSystemResource(filePath);

        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
        formData.add("name", "");
        formData.add("datafile", fileResource);

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);

        RestTemplate restTemplate = new RestTemplate();
        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);

            removeCallback(filePath);

            String responseBody = responseEntity.getBody();

            ObjectMapper objectMapper = new ObjectMapper();

            JsonNode jsonNode = objectMapper.readTree(responseBody);

            String id = jsonNode.get("data").get("id").asText();

            String newDownloadUrl = uploadUrl + "/" + id;

            return newDownloadUrl;
        } catch (Exception e) {
            System.err.println("Error uploading file: " + e.getMessage());
            throw e;
        }
    }

    public String url2file(String url, String format) throws IOException {
        // Create temp directory if not exists
        File dir = new File(tempDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // Generate temp file path
        String tempFilePath = tempDir + File.separator + System.currentTimeMillis() + "." + format;
        downloadFile(url, tempFilePath);
        return tempFilePath;
    }

    public void downloadFile(String url, String filePath) throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<byte[]> response = restTemplate.getForEntity(url, byte[].class);

        if (response.getStatusCode() == HttpStatus.OK) {
            File file = new File(filePath);
            org.apache.commons.io.FileUtils.writeByteArrayToFile(file, response.getBody());
        } else {
            throw new IOException("Failed to download file: " + response.getStatusCode());
        }
    }

    public void removeCallback(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }


    public List<Title> parseCatalog(String fullText) {
        List<Title> titles = new ArrayList<>();
        String[] lines = fullText.split("\n");

        for (String line : lines) {
            if (line.startsWith("# ")) {
                titles.add(new Title(1, line.substring(2)));
            } else if (line.startsWith("## ")) {
                titles.add(new Title(2, line.substring(3)));
            } else if (line.startsWith("### ")) {
                titles.add(new Title(3, line.substring(4)));
            } else if (line.startsWith("#### ")) {
                titles.add(new Title(4, line.substring(5)));
            }
        }

        return titles;
    }

    public String chat(List<Message> messageList) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(openaiApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, String> responseFormat = new HashMap<>();
        responseFormat.put("type", "json_object");
        OpenAIRequest request = new OpenAIRequest("gpt-3.5-turbo-1106", false, responseFormat, messageList);

        HttpEntity<OpenAIRequest> requestEntity = new HttpEntity<>(request, headers);

        // Perform POST request to OpenAI API
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> responseEntity;
        try {

            responseEntity = restTemplate.exchange(openaiBaseUrl + "/chat/completions", HttpMethod.POST, requestEntity, String.class);
        } catch (Exception e) {
            // Handle exception if request fails
            System.err.println("Error chatting with OpenAI: " + e.getMessage());
            throw e;
        }

        // Return response body as String
        return responseEntity.getBody();
    }

    public BasicInformationResult parseBasicInformation(List<List<PageContent>> paperContent) throws IOException {
        String paperContentText = paperContent.get(0).get(0).getMd();

        List<Message> messages = new ArrayList<>();
        Message systemMessage = new Message("system", "You are a helpful assistant designed to output JSON.");
        messages.add(systemMessage);

        Message userMessage = new Message("user", "我将发给你一份学术论文的首页。请你从中提取出论文标题和作者列表的信息。" +
                "返回格式如下，如果标题没有提取出来，返回空字符串，如果作者列表没有提取出来，返回空数组, 原文是什么语言，你就用什么语言，请按照如下的返回格式:" +
                "{\\\"title\\\":\\\"论文标题\\\", \\\"authors\\\":[\\\"作者1\\\", \\\"作者2\\\", \\\"作者3\\\"]}," +
                "以下是论文首页内容:%s\" }\n" + paperContentText);
        messages.add(userMessage);

        String completion = chat(messages);
        completion = completion.replace("\n", "");

        JsonObject jsonObject = JsonParser.parseString(completion).getAsJsonObject();
        String title = jsonObject.get("title").getAsString();
        List<String> authors = new ArrayList<>();
        jsonObject.get("authors").getAsJsonArray().forEach(jsonElement -> authors.add(jsonElement.getAsString()));

        StringBuilder fullTextBuilder = new StringBuilder();
        for (List<PageContent> pageContents : paperContent) {
            for (PageContent pageContent : pageContents) {
                fullTextBuilder.append(pageContent.getMd());
            }
        }

        String fullText = fullTextBuilder.toString();
        List<Title> catalog = parseCatalog(fullText);

        String indexPicUrl = paperContent.get(0).get(0).getUrl();
        String newIndexPicUrl = URLuploadfile(indexPicUrl, true, "png");

        RenewPicURLsResult renewPicURLsResult = RenewPicURLs(fullText);

        return new BasicInformationResult(title, authors, catalog, renewPicURLsResult.getPicUrls(), newIndexPicUrl, renewPicURLsResult.getNewFullText());
    }

    public RenewPicURLsResult RenewPicURLs(String fullText) throws IOException {
        Pattern pattern = Pattern.compile("<img.*?src=\"(.*?)\".*?/>");
        Matcher matcher = pattern.matcher(fullText);

        List<String> imgUrls = new ArrayList<>();
        while (matcher.find()) {
            imgUrls.add(matcher.group(1));
        }

        List<String> newImgUrls = new ArrayList<>();
        for (String imgUrl : imgUrls) {
            String newUrl = URLuploadfile(imgUrl, true, "png");
            newImgUrls.add(newUrl);
        }

        String newFullText = fullText;
        for (int i = 0; i < imgUrls.size(); i++) {
            newFullText = newFullText.replace(imgUrls.get(i), newImgUrls.get(i));
        }

        return new RenewPicURLsResult(newImgUrls, newFullText);
    }


    private String saveTempFile(MultipartFile file) throws IOException {
        File tempFile = new File(tempDir, System.currentTimeMillis() + "_" + file.getOriginalFilename());
        file.transferTo(tempFile);
        return tempFile.getAbsolutePath();
    }

    private List<PageContent> parsePageContents(JsonArray jsonArray) {
        List<PageContent> pageContents = new ArrayList<>();
        jsonArray.forEach(jsonElement -> pageContents.add(new PageContent(jsonElement.getAsJsonObject().get("md").getAsString(), jsonElement.getAsJsonObject().get("url").getAsString())));
        return pageContents;
    }

    @Data
    public class Title {
        private int level;
        private String title;

        public Title(int level, String title) {
            this.level = level;
            this.title = title;
        }

        // Getters and Setters
    }

    @Data
    public static class Message {
        private String role;
        private String content;
        public Message() {
            // 无参构造函数，Jackson需要
        }

        public Message(@JsonProperty("role") String role, @JsonProperty("content") String content) {
            this.role = role;
            this.content = content;
        }

    }

    @Data
    public class PageContent {
        private String md;
        private String url;

        public PageContent(String md, String url) {
            this.md = md;
            this.url = url;
        }

        // Getters and Setters
    }
    @Data
    public class BasicInformationResult {
        private String title;
        private List<String> authors;
        private List<Title> catalog;
        private List<String> picUrls;
        private String indexPicUrl;
        private String fullText;

        public BasicInformationResult(String title, List<String> authors, List<Title> catalog, List<String> picUrls, String indexPicUrl, String fullText) {
            this.title = title;
            this.authors = authors;
            this.catalog = catalog;
            this.picUrls = picUrls;
            this.indexPicUrl = indexPicUrl;
            this.fullText = fullText;
        }

        public JsonObject toJson() {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("title", title);
            jsonObject.add("authors", new Gson().toJsonTree(authors));
            jsonObject.add("catalog", new Gson().toJsonTree(catalog));
            jsonObject.add("picUrls", new Gson().toJsonTree(picUrls));
            jsonObject.addProperty("indexPicUrl", indexPicUrl);
            jsonObject.addProperty("fullText", fullText);
            return jsonObject;
        }

        // Getters and Setters
    }
    @Data
    public class RenewPicURLsResult {
        private List<String> picUrls;
        private String newFullText;

        public RenewPicURLsResult(List<String> picUrls, String newFullText) {
            this.picUrls = picUrls;
            this.newFullText = newFullText;
        }

        // Getters and Setters
    }

    @Data
    private static class OpenAIRequest {
        private String model;
        private boolean stream;
        private Object response_format;
        private List<Message> messages;

        public OpenAIRequest(String model, boolean stream, Object response_format, List<Message> messages) {
            this.model = model;
            this.stream = stream;
            this.response_format = response_format;
            this.messages = messages;
        }
    }
}
