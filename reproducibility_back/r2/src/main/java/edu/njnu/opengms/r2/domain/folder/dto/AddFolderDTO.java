package edu.njnu.opengms.r2.domain.folder.dto;

import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.folder.Folder;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @ClassName AddModelServiceDTO
 * @Description ass we see
 * @Author sun_liber
 * @Date 2020/4/29
 * @Version 1.0.0
 */
@Data
@Builder
public class AddFolderDTO implements ToDomainConverter<Folder> {
    String name;//创建的文件夹名称
    String parent;
    Integer level;
    String tagId;// 如果是在scenario中，那么folder的name就是scenario的name？
    List<String> children;
    List<String> dataList;//这个文件夹下存储的 data id
    String creatorId;
}

