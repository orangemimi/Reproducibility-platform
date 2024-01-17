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
public interface ScenarioRepository extends MongoRepository<Scenario,String> {
//    Scenario findById(String id);
    List<Scenario> findAllByProjectId(String projectId);
    Optional<Scenario> findById(String id);
    Scenario findByInitialScenarioId(String id);
}
