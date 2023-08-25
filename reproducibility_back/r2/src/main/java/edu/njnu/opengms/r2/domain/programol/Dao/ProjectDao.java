package edu.njnu.opengms.r2.domain.programol.Dao;

import edu.njnu.opengms.r2.domain.programol.Project;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author TRY
 * @Date 2023/3/29 11:03
 * @Version 1.0
 */
@Repository
public interface ProjectDao extends MongoRepository<Project,String> {
    Project findByProjectName(String projectName);
    Integer deleteProjectByProjectName(String ProjectName);
    List<Project> findByWorkspace(String workspace);
}
