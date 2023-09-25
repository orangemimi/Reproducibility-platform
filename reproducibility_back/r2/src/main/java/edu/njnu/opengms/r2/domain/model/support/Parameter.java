package edu.njnu.opengms.r2.domain.model.support;

import cn.hutool.json.JSONObject;
import lombok.Data;

/**
 * @ClassName Parameter
 * @Description ass we see
 * @Author sun_liber
 * @Date 2020/3/18
 * @Version 1.0.0
 */
@Data
public class Parameter {
    public String name;
    public String eventId;
    public Boolean isOptional=false;
    public String type;
    public String description;
    public String tooltip;//
    public String value;
    public String dataId;
    public JSONObject datasetItem;
}
