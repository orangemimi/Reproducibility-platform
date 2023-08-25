package edu.njnu.opengms.r2.domain.programol;

import cn.hutool.core.util.IdUtil;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;
import java.util.List;

/**
 * @Author TRY
 * @Date 2023/3/29 10:46
 * @Version 1.0
 */
@Data
public class ProgramFile {
    @Id
    @ApiModelProperty(value = "id",hidden = true)
    String id= IdUtil.objectId();
    String fileName;
    String filePath;
    boolean isFolder;
    String projectName;
    List<String> childrenId;
    String parentId;
    long size;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone="GMT+8")
    Date createTime=new Date(); //创建时间
    String userId; //上传者ID

}
