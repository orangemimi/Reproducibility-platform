package edu.njnu.opengms.r2.domain.modelInstance;

import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.modelInstance.dto.UpdateModelInstanceDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author ：Zhiyi
 * @Date ：2024/1/5 19:22
 * @modified By：
 * @version: 1.0.0
 */
@Service
public class ModelInstanceService {

    @Autowired
    ModelInstanceRepository modelInstanceRepository;

    public ModelInstance updateInstance(String id, UpdateModelInstanceDTO update) {
        ModelInstance modelInstance = modelInstanceRepository.findById(id).orElseThrow(MyException::noObject);
        update.updateTo(modelInstance);
        return modelInstanceRepository.save( modelInstance);
    }
}
