package edu.njnu.opengms.r2.data;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @InterfaceName DataRepository
 * @Description ass we see
 * @Author sun_liber
 * @Date 2020/4/29
 * @Version 1.0.0
 */
@Repository
public interface DataItemRepository extends MongoRepository<DataItem,String> {
    Page<DataItem> findAllByDescriptionIsNotNull(Pageable pageable);
}
