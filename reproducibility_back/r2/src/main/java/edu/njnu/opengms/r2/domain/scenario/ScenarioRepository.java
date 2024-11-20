package edu.njnu.opengms.r2.domain.scenario;

import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

/**
 * @Author ：Zhiyi
 * @Date ：2021/4/1 22:43
 * @modified By：
 * @version: 1.0.0
 */
public interface ScenarioRepository extends MongoRepository<Scenario, String> {
    //    Scenario findById(String id);
    List<Scenario> findAllByProjectId(String projectId);

    // 现在一个project只有一个scenario了，上面那个作废
    Scenario findByProjectId(String projectId);


    Optional<Scenario> findById(String id);

    Scenario findByInitialScenarioId(String id);


}
