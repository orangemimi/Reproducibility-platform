package edu.njnu.opengms.r2.domain.project.support;

import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: Yiming
 * @Date: 2022/03/05/14:38
 * @Description:
 */
@Data
public class Citation {
    String type;
    String name;
    String author;
    String source;
//    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
//    @DateTimeFormat(pattern = "yyyy-MM-dd")
    Date date;
    String dateType;    //year, month, date
}
