package edu.njnu.opengms.r2.domain.programol.dto;

import lombok.Data;

import java.util.List;

/**
 * @Author TRY
 * @Date 2023/3/30 21:43
 * @Version 1.0
 */
@Data
public class ProgramFileDTO {
    String type;
    String id;
    String filePath;
    String label;
    List<ProgramFileDTO> children;

    public ProgramFileDTO(String id,String type, String filePath, String label) {
        this.id=id;
        this.type = type;
        this.filePath = filePath;
        this.label = label;
    }

    public ProgramFileDTO(String id,String type, String filePath, String label, List<ProgramFileDTO> children) {
        this.id=id;
        this.type = type;
        this.filePath = filePath;
        this.label = label;
        this.children = children;
    }
}
