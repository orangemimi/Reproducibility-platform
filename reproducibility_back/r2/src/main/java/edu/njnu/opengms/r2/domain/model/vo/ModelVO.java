package edu.njnu.opengms.r2.domain.model.vo;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName ModelServiceVO
 * @Description ass we see
 * @Author sun_liber
 * @Date 2020/5/13
 * @Version 1.0.0
 */
@Data
public class ModelVO {
    public String name;
    public String description;
    public String creator;
    public Date createTime;
    String id;
}
