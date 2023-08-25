package edu.njnu.opengms.r2.domain.programol.Dao;

import edu.njnu.opengms.r2.domain.programol.Docker;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @Author TRY
 * @Date 2023/4/6 14:03
 * @Version 1.0
 */
@Repository
public interface DockerDao extends MongoRepository<Docker,String> {
}
