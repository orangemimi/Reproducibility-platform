package edu.njnu.opengms.container.domain.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @InterfaceName ModelServiceRepository
 * @Description @Query只能将字段对应的值赋值为null，但是仍然会对外暴露出字段名   @Query (fields="{ 'name' : 1,'description':1,'createTime':1}")
 *              这里使用了VO进行处理
 * @Author sun_liber
 * @Date 2020/4/29
 * @Version 1.0.0
 */
@Repository
public interface ModelRepository extends MongoRepository<ModelService,String> {
    Optional<ModelService> findById(String id);
    Optional<ModelService> findByServiceId(String id);
//    List<ModelVO> findByPrivacyInAndNameContainsIgnoreCase(String privacy, String name);
    List<ModelService> findByNameContainsIgnoreCase(String name);
//    Optional<ModelInfo> findByIdAndCreatorId(String id,String userId);
//    Page<ModelInfo> findByPrivacyInOrCreatorId(List<String> privacyList, String creatorId, Pageable pageable);
    Page<ModelService> findByPrivacy(String privacy, Pageable pageable);
//    Page<ModelServiceVO> getByNameContainsIgnoreCase(String name, Pageable pageable);
}
