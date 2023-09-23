package edu.njnu.opengms.r2.domain.project;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @Author ：Zhiyi
 * @Date ：2020/12/4 14:44
 * @modified By：
 * @version: 1.0.0
 */
@Repository
public interface ProjectRepository extends MongoRepository<Project,String> {
    Optional<Project> findById(String id);
    Optional<Project> findByIdAndCreatorId(String id,String userId);
    Page<Project> findByPrivacyInOrCreatorId(List<String> privacyList, String creatorId, Pageable pageable);
    Page<Project> findByPrivacy(String privacy, Pageable pageable);

}
