package edu.njnu.opengms.r2.domain.programol.Dao;

import edu.njnu.opengms.r2.domain.programol.WorkSpace;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author TRY
 * @Date 2023/3/29 11:01
 * @Version 1.0
 */
@Repository
public interface WorkSpaceDao extends MongoRepository<WorkSpace,String> {
    WorkSpace findByUserId(String userId);
}
