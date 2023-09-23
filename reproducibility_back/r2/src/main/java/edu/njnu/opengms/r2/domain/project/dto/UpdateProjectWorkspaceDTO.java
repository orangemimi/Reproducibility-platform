package edu.njnu.opengms.r2.domain.project.dto;


import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.project.Project;
import edu.njnu.opengms.r2.domain.workspace.Workspace;
import lombok.Data;

/**
 * @Author ：Zhiyi
 * @Date ：2020/12/10 21:36
 * @modified By：
 * @version: 1.0.0
 */
@Data
public class UpdateProjectWorkspaceDTO implements ToDomainConverter<Project> {
    Workspace workspace;
}
