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
public class UpdateFolderChildrenDTO implements ToDomainConverter<Folder> {

    List<String> children;

}
