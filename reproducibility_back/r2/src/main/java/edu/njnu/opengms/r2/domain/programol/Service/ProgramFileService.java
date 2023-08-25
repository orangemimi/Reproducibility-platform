package edu.njnu.opengms.r2.domain.programol.Service;


import edu.njnu.opengms.r2.domain.programol.Dao.ProgramFileDao;
import edu.njnu.opengms.r2.domain.programol.ProgramFile;
import edu.njnu.opengms.r2.domain.programol.dto.CreateProgramFileDTO;
import edu.njnu.opengms.r2.utils.FileUtils;
import edu.njnu.opengms.r2.utils.JsonResult;
import edu.njnu.opengms.r2.utils.MyFileUtils;
import edu.njnu.opengms.r2.utils.ResultUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

/**
 * @Author TRY
 * @Date 2023/3/29 11:11
 * @Version 1.0
 */
@Slf4j
@Service
public class ProgramFileService {
    @Autowired
    ProgramFileDao programFileDao;

    @Value("E:/test/workspace/temp")
    private String tempFileDir;



    public JsonResult CreateFile(CreateProgramFileDTO createProgramFileDTO){
        try {
            if (createProgramFileDTO.isFolder()){
                File folder=new File(createProgramFileDTO.getFilePath());
                if (!folder.isDirectory()) {
                    folder.mkdir();
                }
                ProgramFile File = new ProgramFile();
                BeanUtils.copyProperties(createProgramFileDTO,File);
                programFileDao.save(File);
                return ResultUtils.success("创建文件夹成功");
            }
            else {
                File file=new File(createProgramFileDTO.getFilePath());
                if (!file.isFile()) {
                    file.createNewFile();
                }
                ProgramFile File = new ProgramFile();
                BeanUtils.copyProperties(createProgramFileDTO,File);
                programFileDao.save(File);
                return ResultUtils.success("创建文件成功");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtils.success("创建失败");
        }
    }

    public JsonResult deleteFile(String id) {
        boolean delete = delete(id, true);
        if (delete){
            return ResultUtils.success("删除成功");
        }
        else {
            return ResultUtils.error("删除失败");
        }
    }
    
    public JsonResult renameFile(String id,String newName){
        Optional<ProgramFile> file = programFileDao.findById(id);
        if (!file.isPresent()){
            return ResultUtils.error("未找到文件");
        }
        ProgramFile programFile = file.get();
        String filePath = programFile.getFilePath();
        File renfile=new File(filePath);
        File parentFile = renfile.getParentFile();
        String absolutePath = parentFile.getAbsolutePath();
        programFile.setFileName(newName);
        programFile.setFilePath(absolutePath+'/'+newName);
        programFileDao.save(programFile);
        renfile.renameTo(new File(absolutePath+'/'+newName));
        return ResultUtils.success("重命名成功");
    }

    public boolean delete(String id,boolean ifsuccess){
        Optional<ProgramFile> file = programFileDao.findById(id);
        if (!file.isPresent()){
            ifsuccess=false;
            return ifsuccess;
        }
        ProgramFile programFile = file.get();
        if (programFile.isFolder()){
            String parentId = programFile.getId();
            List<ProgramFile> children = programFileDao.findProgramFilesByParentId(parentId);
            for (ProgramFile child : children) {
                String childId = child.getId();
                ifsuccess=delete(childId,ifsuccess);
            }

        }
        File delfile=new File(programFile.getFilePath());
        delfile.delete();
        programFileDao.deleteById(id);
        return ifsuccess;
    }

    public void downloadFile(String fileId, HttpServletResponse response) {
        Optional<ProgramFile> programFile = programFileDao.findById(fileId);
        if (!programFile.isPresent()) {
            System.out.println("不存在此文件");
        }
        ProgramFile file = programFile.get();
        File donwlfile=new File(file.getFilePath());
        if (file.isFolder()) {
            String zipPath = tempFileDir+'/'+file.getFileName()+".zip";
            File zipFile = new File(zipPath);
            if (zipFile.exists()){
                MyFileUtils.downloadFile(zipFile,response);
            }
            else {
                FileOutputStream zipFos = null;
                try {
                    zipFos = new FileOutputStream(zipFile);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                FileUtils.toZip(file.getFilePath(), zipFos,true);
                MyFileUtils.downloadFile(zipFile,response);
            }
        }else{
            MyFileUtils.downloadFile(donwlfile,response);
        }
    }

//    @Scheduled(cron = "0 30 8 * * ?  ")
//    @Scheduled(cron = "0 58 * * * ?  ")
//    public void deleteTemp(){
//        System.out.println("删除临时文件");
//        File file=new File(tempFileDir);
//        File[] files = file.listFiles();
//        for (File file1 : files) {
//            file1.delete();
//        }
//    }

    public JsonResult uploadFile(String parentId, MultipartFile upFile) {
        try{
            Optional<ProgramFile> programFileDB = programFileDao.findById(parentId);
            if (!programFileDB.isPresent()){
                return ResultUtils.error("父文件不存在");
            }
            if (upFile.isEmpty()) {
                return ResultUtils.error("文件为空");
            }
            ProgramFile ParentprogramFile = programFileDB.get();
            if (!ParentprogramFile.isFolder()) {
                return ResultUtils.error("创建出错");
            }
            String filePath = ParentprogramFile.getFilePath();
            String projectName = ParentprogramFile.getProjectName();
            String userId = ParentprogramFile.getUserId();
            ProgramFile programFile=new ProgramFile();
            String fileName = upFile.getOriginalFilename(); //eg: XXX.png
            File fileToUp = new File(filePath, fileName);//eg: E:\\TEMP\\XXX.zip

            programFile.setUserId(userId);
            programFile.setFolder(false);
            programFile.setFileName(fileName);
            programFile.setParentId(parentId);
            programFile.setFilePath(fileToUp.getAbsolutePath().replace("\\","/"));
            programFile.setProjectName(projectName);
            programFile.setSize(fileToUp.length());

            upFile.transferTo(fileToUp);
            programFileDao.save(programFile);

//            JSONObject o=new JSONObject();
//            o.put("imgWebPath",imgWebPath);

            return ResultUtils.success("上传成功");
        }catch (Exception e){
            log.error(e.getMessage());
            return ResultUtils.error("上传失败"+e.getMessage());
        }
    }

    public JsonResult openProgramFile(String id) {
        Optional<ProgramFile> programFileDB = programFileDao.findById(id);
        if (!programFileDB.isPresent()) {
            return ResultUtils.error("文件不存在");
        }
        ProgramFile programFile = programFileDB.get();
        String filePath = programFile.getFilePath();
        byte[] bytes = new byte[0];
        try {
            bytes = Files.readAllBytes(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String content = new String(bytes, StandardCharsets.UTF_8);
        return ResultUtils.success(content);
    }

    public JsonResult saveEdit(String id, String content) {
        Optional<ProgramFile> programFileDB = programFileDao.findById(id);
        if (!programFileDB.isPresent()) {
            return ResultUtils.error("文件不存在");
        }
        ProgramFile programFile = programFileDB.get();
        String filePath = programFile.getFilePath();
        try {
            Files.write(Paths.get(filePath),content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            return ResultUtils.error("文件写入失败");
        }
        return ResultUtils.success("文件写入成功");
    }
}
