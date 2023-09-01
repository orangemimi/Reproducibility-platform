package edu.njnu.opengms.r2.domain.data;

import com.google.common.collect.Lists;
import edu.njnu.opengms.common.utils.JsonResult;
import edu.njnu.opengms.common.utils.ResultUtils;
import edu.njnu.opengms.r2.domain.data.dto.AddDataItemDTO;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/23 17:08
 * @modified By：
 * @version: 1.0.0
 */

@RestController
@RequestMapping(value = "/data")
public class DataItemController {
    @Autowired
    DataItemRepository dataItemRepository;

    @Value("${storages.local.path}")
    String pathString;

    @Autowired
    FileStorageRepository fileStorageRepository;

    @RequestMapping (value = "/addByFileToPublic",method = RequestMethod.POST)
    public JsonResult addByFileToPublic(@RequestPart("file") MultipartFile file,
                                        @RequestPart ("name") String name,
//    @RequestPart ("resourceUrl") String resourceUrl,
                                        @RequestPart ("description") String description
    ) throws IOException {
        FileStorage insert = fileStorageRepository.insert(uploadFile(file));
        DataItem dataService = new DataItem();
        AddDataItemDTO a = AddDataItemDTO.builder().key(insert.getKey())
                .name(name)
//                .resourceUrl(resourceUrl)
                .description(description)
                .isIntermediate(false)
                .build();
        a.setKey(insert.getKey());
        a.convertTo(dataService);
        DataItem  insert1= dataItemRepository.insert(dataService);
        JsonResult success = ResultUtils.success(insert1);
        return success;
    }

    @RequestMapping (value = "/addByFile",method = RequestMethod.POST)
    public JsonResult addByFile(@RequestPart ("file") MultipartFile file) throws IOException {
        FileStorage insert = fileStorageRepository.insert(uploadFile(file));
        DataItem dataService=new DataItem();
        //加入时间戳
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = sdf.format(new Date());
        String baseName = FilenameUtils.getBaseName(insert.getName());
        String extension = FilenameUtils.getExtension(insert.getName());
        AddDataItemDTO a = AddDataItemDTO.builder().key(insert.getKey()).name(baseName + "_" + date + "." + extension).build();
        a.setKey(insert.getKey());
        a.convertTo(dataService);
        DataItem  insert1= dataItemRepository.insert(dataService);
        JsonResult success = ResultUtils.success(insert1);
        return success;
    }

    private FileStorage uploadFile(MultipartFile file) throws IOException {
        String key= UUID.randomUUID().toString();
        String name=file.getOriginalFilename();
        File dir=new File(String.valueOf(Paths.get(pathString).resolve(key)));
        dir.mkdir();
        Files.copy(file.getInputStream(), Paths.get(pathString).resolve(key).resolve(name), StandardCopyOption.REPLACE_EXISTING);
        return FileStorage.builder().key(key).name(name).contentType(file.getContentType()).size(file.getSize()).build();
    }

    @RequestMapping (value = "/listByIds", method = RequestMethod.GET)
    public JsonResult listByIds(@RequestParam("ids") List<String> ids){
        return ResultUtils.success(Lists.newArrayList(dataItemRepository.findAllById(ids)));
    }

    @RequestMapping (value = "/all", method = RequestMethod.GET)
    public JsonResult findAll(){
        return ResultUtils.success(dataItemRepository.findAll());
    }



}
