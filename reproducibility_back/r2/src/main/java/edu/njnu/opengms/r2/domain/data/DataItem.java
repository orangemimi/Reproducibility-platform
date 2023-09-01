package edu.njnu.opengms.r2.domain.data;

import edu.njnu.opengms.common.entity.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * @Author ：Zhiyi
 * @Date ：2023/8/25 16:38
 * @modified By：
 * @version: 1.0.0
 */
@Data
@Document
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DataItem extends BaseEntity {
    @Id
    String id;
    String name;
    String description;
    Boolean isInitial;//是否是初始数据
    Boolean isIntermediate;//是否是中间数据
    Boolean isReproduced;//是否是复现的结果
    Boolean isParameter;//是否是参数
    String key;
    String value;//resource url or value
    String contributorId;// if is reproduced intermediate data , the contributor is the executor

}
