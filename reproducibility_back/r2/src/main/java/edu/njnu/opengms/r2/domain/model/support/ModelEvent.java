package edu.njnu.opengms.r2.domain.model.support;

import cn.hutool.json.JSONObject;
import lombok.Data;


/**
 * @ClassName ModelEvent
 * @Description ass we see
 * @Author sun_liber
 * @Date 2020/3/18
 * @Version 1.0.0
 */
@Data
public class ModelEvent {
    public String eventId;//自动生成
    public String name;
    public Boolean isOptional=false;
    public String description;
    public String dataId;
    public JSONObject datasetItem;

}
