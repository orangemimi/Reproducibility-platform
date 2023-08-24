package edu.njnu.opengms.common.domain.container.data;


import edu.njnu.opengms.common.entity.BaseEntity;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @ClassName edu.njnu.opengms.container.domain.data.DataService
 * @Description ass we see
 * @Author sun_liber
 * @Date 2020/3/17
 * @Version 1.0.0
 */


@Data
@Document
public class DataInfo extends BaseEntity {
    @Id
    String id;
    String description;
    Boolean isInitial;//是否是初始数据
    Boolean isIntermediate;//是否是中间数据
    Boolean isReproduced;//是否是复现的结果
    Boolean isParameter;//是否是参数

    String value;//resource url or value
    String contributorId;// if is reproduced intermediate data , the contributor is the executor

}
