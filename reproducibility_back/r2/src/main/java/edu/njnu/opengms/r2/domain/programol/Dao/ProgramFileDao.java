package edu.njnu.opengms.r2.domain.programol.Dao;

import edu.njnu.opengms.r2.domain.programol.ProgramFile;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Author TRY
 * @Date 2023/3/29 11:04
 * @Version 1.0
 */
@Repository
public interface ProgramFileDao extends MongoRepository<ProgramFile,String> {
    Integer deleteAllByProjectNameAndUserId(String projectName,String UserId);
    ProgramFile findProgramFileByFilePath(String filePath);
    Integer deleteAllByParentId(String ParentId);
    List<ProgramFile> findProgramFilesByParentId(String ParentId);
}
