//package edu.njnu.opengms.r2.domain.AIAssistant;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.io.FileSystemResource;
//import org.springframework.http.*;
//import org.springframework.stereotype.Service;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.client.RestTemplate;
//
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//@Service
//public class AiServiceback {
//
//    private String tempDir = "files";
//
//    @Value("${dataContainer}")
//    private String dataContainer;
//
//    private String uploadUrl = "http://" + dataContainer + ":8083/data";
//
//    private String apiKey ="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJVc2VySUQiOjk2NiwiVXNlclJvbGUiOiJjdXN0b21lciIsImV4cCI6MTcxNjM1NjUyMCwiaWF0IjoxNzE2MDk3MzIwfQ.XrmjakWt7RP_lvplUwPB_oCPrLfWB-_r51O2KFHje7I";;
//
//    private String parsePdfApiUrl = "https://api.doc2x.noedgeai.com/api/platform/pdf";
//
//    private String openaiApiKey = "sk-bC5D3W6x2zxESp8NDfD051C9B8824364AfAf90701eD15fA9";
//
//    private String openaiBaseUrl = "https://kkkc.net/v1";
//
//
//    public void removeCallback(String filePath) {
//        Path path = Paths.get(filePath);
//        try {
//            Files.delete(path);
//            System.out.println("Temporary file deleted successfully.");
//        } catch (IOException e) {
//            System.err.println("Error deleting temporary file: " + e.getMessage());
//        }
//    }
//
//    public void downloadFile(String urlStr, String filePath) throws IOException {
//        URL url = new URL(urlStr);
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//
//        try (InputStream inputStream = connection.getInputStream();
//             FileOutputStream outputStream = new FileOutputStream(filePath)) {
//
//            byte[] buffer = new byte[4096];
//            int bytesRead;
//            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputStream.write(buffer, 0, bytesRead);
//            }
//
//            System.out.println("File downloaded successfully.");
//        } catch (IOException e) {
//            Files.deleteIfExists(Paths.get(filePath));
//            throw e;
//        } finally {
//            connection.disconnect();
//        }
//    }
//
//    public String url2file(String url, String format) throws IOException {
//        // Determine the current working directory
//        String currentDir = System.getProperty("user.dir");
//        Path tempDirPath = Paths.get(currentDir, tempDir);
//
//        // Create temporary directory if it doesn't exist
//        if (!Files.exists(tempDirPath)) {
//            Files.createDirectories(tempDirPath);
//        }
//
//        // Create temporary file path
//        String tempFileName = System.currentTimeMillis() + "." + format;
//        Path tempFilePath = tempDirPath.resolve(tempFileName);
//
//        // Download file to temporary file
//        downloadFile(url, tempFilePath.toString());
//
//        return tempFilePath.toString();
//    }
//
//    public String URLuploadfile(String url, boolean isURL, String type) throws IOException {
//        String filePath = url;
//
//        // If isURL is true, download file to temporary directory
//        if (isURL) {
//            try {
//                filePath = url2file(url, type);
//            } catch (IOException e) {
//                throw new IOException("Error downloading file from URL: " + e.getMessage());
//            }
//        }
//
//        // Create FormData for file upload
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        FileSystemResource fileResource = new FileSystemResource(filePath);
//
//        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
//        formData.add("name", "");
//        formData.add("datafile", fileResource);
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);
//
//        // Perform POST request to uploadUrl
//        RestTemplate restTemplate = new RestTemplate();
//        try {
//            ResponseEntity<String> responseEntity = restTemplate.exchange(uploadUrl, HttpMethod.POST, requestEntity, String.class);
//
//            // Clean up temporary file after upload
//            removeCallback(filePath);
//
//            // Process response to get new download URL
//            String newUrl = responseEntity.getBody(); // Assuming response body contains new ID or URL
//            String newDownloadUrl = uploadUrl + "/" + newUrl;
//
//            return newDownloadUrl;
//        } catch (Exception e) {
//            // Handle exception if upload fails
//            System.err.println("Error uploading file: " + e.getMessage());
//            throw e;
//        }
//    }
//    public String parsePdf(String filePath) throws IOException {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(apiKey);
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//
//        FileSystemResource fileResource = new FileSystemResource(filePath);
//
//        MultiValueMap<String, Object> formData = new LinkedMultiValueMap<>();
//        formData.add("file", fileResource);
//        formData.add("ocr", "true");
//        formData.add("straming", "false");
//
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(formData, headers);
//
//        // Perform POST request to parsePdfApiUrl
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<String> responseEntity;
//        try {
//            responseEntity = restTemplate.exchange(parsePdfApiUrl, HttpMethod.POST, requestEntity, String.class);
//        } catch (Exception e) {
//            // Handle exception if request fails
//            System.err.println("Error parsing PDF: " + e.getMessage());
//            throw e;
//        }
//
//        // Clean up temporary file after parsing
//        removeCallback(filePath);
//
//        // Process response to get parsed text
//        if (responseEntity.getStatusCode() == HttpStatus.OK) {
//            String responseBody = responseEntity.getBody();
//            // Remove empty lines from response body
//            String[] lines = responseBody.split("\n");
//            StringBuilder textBuilder = new StringBuilder();
//            for (String line : lines) {
//                if (!line.trim().isEmpty()) {
//                    textBuilder.append(line).append("\n");
//                }
//            }
//            return textBuilder.toString();
//        } else {
//            System.err.println("[ERROR] status code: " + responseEntity.getStatusCodeValue() + ", body: " + responseEntity.getBody());
//            return null;
//        }
//    }
//
//    public Object chat(List<String> messageList) {
//        HttpHeaders headers = new HttpHeaders();
//        headers.setBearerAuth(openaiApiKey);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//
//        OpenAIRequest request = new OpenAIRequest("gpt-3.5-turbo-16k", false, "json_object", messageList);
//
//        HttpEntity<OpenAIRequest> requestEntity = new HttpEntity<>(request, headers);
//
//        // Perform POST request to OpenAI API
//        RestTemplate restTemplate = new RestTemplate();
//        ResponseEntity<Object> responseEntity;
//        try {
//            responseEntity = restTemplate.exchange(openaiBaseUrl + "/chat/completions", HttpMethod.POST, requestEntity, Object.class);
//        } catch (Exception e) {
//            // Handle exception if request fails
//            System.err.println("Error chatting with OpenAI: " + e.getMessage());
//            throw e;
//        }
//
//        // Return response object
//        return responseEntity.getBody();
//    }
//
//    // Inner class to represent the request body for OpenAI API
//    private static class OpenAIRequest {
//        private String model;
//        private boolean stream;
//        private String response_format;
//        private List<String> messages;
//
//        public OpenAIRequest(String model, boolean stream, String response_format, List<String> messages) {
//            this.model = model;
//            this.stream = stream;
//            this.response_format = response_format;
//            this.messages = messages;
//        }
//
//        // Getters and setters
//        public String getModel() {
//            return model;
//        }
//
//        public void setModel(String model) {
//            this.model = model;
//        }
//
//        public boolean isStream() {
//            return stream;
//        }
//
//        public void setStream(boolean stream) {
//            this.stream = stream;
//        }
//
//        public String getResponse_format() {
//            return response_format;
//        }
//
//        public void setResponse_format(String response_format) {
//            this.response_format = response_format;
//        }
//
//        public List<String> getMessages() {
//            return messages;
//        }
//
//        public void setMessages(List<String> messages) {
//            this.messages = messages;
//        }
//    }
//
//    public List<Title> parseCatalog(String paperContent) {
//        List<Title> titles = new ArrayList<>();
//        int currentLevel = 0;
//        String currentTitle = "";
//
//        String[] lines = paperContent.split("\n");
//        for (String line : lines) {
//            if (line.startsWith("# ")) {
//                currentLevel = 1;
//                currentTitle = line.substring(2);
//                titles.add(new Title(currentLevel, currentTitle));
//            } else if (line.startsWith("## ")) {
//                currentLevel = 2;
//                currentTitle = line.substring(3);
//                titles.add(new Title(currentLevel, currentTitle));
//            } else if (line.startsWith("### ")) {
//                currentLevel = 3;
//                currentTitle = line.substring(4);
//                titles.add(new Title(currentLevel, currentTitle));
//            } else if (line.startsWith("#### ")) {
//                currentLevel = 4;
//                currentTitle = line.substring(5);
//                titles.add(new Title(currentLevel, currentTitle));
//            }
//        }
//
//        return titles;
//    }
//
//    // Inner class to represent a title with level
//    public static class Title {
//        private int level;
//        private String title;
//
//        public Title(int level, String title) {
//            this.level = level;
//            this.title = title;
//        }
//
//        // Getters and setters
//        public int getLevel() {
//            return level;
//        }
//
//        public void setLevel(int level) {
//            this.level = level;
//        }
//
//        public String getTitle() {
//            return title;
//        }
//
//        public void setTitle(String title) {
//            this.title = title;
//        }
//    }
//
//    public RenewPicURLsResult renewPicURLs(String fullText) {
//        List<String> imgUrls = extractImgUrls(fullText);
//
//        List<String> newImgUrls = new ArrayList<>();
//        for (String imgUrl : imgUrls) {
//            String newUrl = urlService.uploadFile(imgUrl, true, "png");
//            newImgUrls.add(newUrl);
//        }
//
//        String newFullText = fullText;
//        for (int i = 0; i < imgUrls.size(); i++) {
//            newFullText = newFullText.replace(imgUrls.get(i), newImgUrls.get(i));
//        }
//
//        return new RenewPicURLsResult(newImgUrls, newFullText);
//    }
//
//    private List<String> extractImgUrls(String fullText) {
//        List<String> imgUrls = new ArrayList<>();
//        Pattern pattern = Pattern.compile("<img.*?src=\"(.*?)\".*?\\/?>");
//        Matcher matcher = pattern.matcher(fullText);
//        while (matcher.find()) {
//            imgUrls.add(matcher.group(1));
//        }
//        return imgUrls;
//    }
//
//    // Inner class to represent the result of renewPicURLs method
//    public static class RenewPicURLsResult {
//        private List<String> picUrls;
//        private String newFullText;
//
//        public RenewPicURLsResult(List<String> picUrls, String newFullText) {
//            this.picUrls = picUrls;
//            this.newFullText = newFullText;
//        }
//
//        // Getters
//        public List<String> getPicUrls() {
//            return picUrls;
//        }
//
//        public String getNewFullText() {
//            return newFullText;
//        }
//    }
//
//    public BasicInformationResult parseBasicInformation(String[][] paperContent) throws IOException {
//        // Step 1: Extract title and authors using chat method
//        String paperPage = paperContent[0][0];
//        String messagesContent = String.format(
//                "我将发给你一份学术论文的首页。请你从中提取出论文标题和作者列表的信息。\n" +
//                        "返回格式如下，如果标题没有提取出来，返回空字符串，如果作者列表没有提取出来，返回空数组, 原文是什么语言，你就用什么语言，请按照如下的返回格式:\n" +
//                        "{\"title\":\"论文标题\", \"authors\":[\"作者1\", \"作者2\", \"作者3\"]},\n" +
//                        "以下是论文首页内容:\n%s", paperPage
//        );
//
//        List<Message> messages = new ArrayList<>();
//        messages.add(new Message("system", "You are a helpful assistant designed to output JSON."));
//        messages.add(new Message("user", messagesContent));
//
//        String completion = chat(messages);
//        String result = completion.replace("\n", "");
//
//        Pattern pattern = Pattern.compile("\\{.*?\\}");
//        Matcher matcher = pattern.matcher(result);
//        String jsonString = matcher.find() ? matcher.group(0) : "{}";
//
//        JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
//
//        String title = jsonObject.has("title") ? jsonObject.get("title").getAsString() : "";
//        List<String> authors = new ArrayList<>();
//        if (jsonObject.has("authors")) {
//            jsonObject.get("authors").getAsJsonArray().forEach(author -> authors.add(author.getAsString()));
//        }
//
//        // Step 2: Concatenate the full text of the paper
//        StringBuilder fullTextBuilder = new StringBuilder();
//        for (String[] page : paperContent[0]) {
//            fullTextBuilder.append(page);
//        }
//        String fullText = fullTextBuilder.toString();
//
//        // Step 3: Parse catalog from full text
//        List<Title> catalog = parseCatalog(fullText);
//
//        // Step 4: Process the index picture URL
//        String indexPicUrl = paperContent[0][0];
//        String newIndexPicUrl = URLuploadfile(indexPicUrl, true, "png");
//
//        // Step 5: Renew picture URLs in full text
//        RenewPicURLsResult picURLsResult = renewPicURLs(fullText);
//
//        return new BasicInformationResult(title, authors, catalog, picURLsResult.getPicUrls(), newIndexPicUrl, picURLsResult.getNewFullText());
//    }
//    public static class BasicInformationResult {
//        private String paperTitle;
//        private List<String> paperAuthors;
//        private List<Title> paperCatalog;
//        private List<String> paperPicURLs;
//        private String paperIndexPicURL;
//        private String paperContent;
//
//        public BasicInformationResult(String paperTitle, List<String> paperAuthors, List<Title> paperCatalog,
//                                      List<String> paperPicURLs, String paperIndexPicURL, String paperContent) {
//            this.paperTitle = paperTitle;
//            this.paperAuthors = paperAuthors;
//            this.paperCatalog = paperCatalog;
//            this.paperPicURLs = paperPicURLs;
//            this.paperIndexPicURL = paperIndexPicURL;
//            this.paperContent = paperContent;
//        }
//
//        public String getPaperTitle() {
//            return paperTitle;
//        }
//
//        public List<String> getPaperAuthors() {
//            return paperAuthors;
//        }
//
//        public List<Title> getPaperCatalog() {
//            return paperCatalog;
//        }
//
//        public List<String> getPaperPicURLs() {
//            return paperPicURLs;
//        }
//
//        public String getPaperIndexPicURL() {
//            return paperIndexPicURL;
//        }
//
//        public String getPaperContent() {
//            return paperContent;
//        }
//    }
//    public static class Message {
//        private String role;
//        private String content;
//
//        public Message(String role, String content) {
//            this.role = role;
//            this.content = content;
//        }
//
//        public String getRole() {
//            return role;
//        }
//
//        public String getContent() {
//            return content;
//        }
//    }
//
//}
