package edu.njnu.opengms.r2.domain.dataItem;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @InterfaceName ModelServiceRepository
 * @Description @Query只能将字段对应的值赋值为null，但是仍然会对外暴露出字段名   @Query (fields="{ 'name' : 1,'description':1,'createTime':1}")
 *              这里使用了VO进行处理
 * @Author sun_liber
 * @Date 2020/4/29
 * @Version 1.0.0
 */
@Repository
public interface DataItemRepository extends MongoRepository<DataItem,String> {
    List<DataItem> findAllByIdIn(List<String> id);
    List<DataItem> findAllById(List<String> id);


}
