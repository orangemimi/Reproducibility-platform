package edu.njnu.opengms.r2.domain.data.dto;


import edu.njnu.opengms.common.dto.ToDomainConverter;
import edu.njnu.opengms.r2.domain.data.DataItem;
import lombok.Builder;
import lombok.Data;

/**
 * @ClassName AddDataServiceDTO
 * @Description ass we see
 * @Author sun_liber
 * @Date 2020/4/29
 * @Version 1.0.0
 */
@Data
@Builder
public class AddDataItemDTO implements ToDomainConverter<DataItem> {
    String description;
    Boolean isInitial;//是否是初始数据
    Boolean isIntermediate;//是否是中间数据
    Boolean isReproduced;//是否是复现的结果
    Boolean isParameter;//是否是参数
    String key;
    String name;
    String value;//resource url or value
    String contributorId;// if is reproduced intermediate data , the contributor is the executor

}
