package edu.njnu.opengms.r2.domain.assessment;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.AssessmentMethod.AssessmentMethodRepository;
import edu.njnu.opengms.r2.domain.assessment.dto.AddAssessmentDTO;
import edu.njnu.opengms.r2.domain.assessment.dto.UpdateAssessmentDTO;
import edu.njnu.opengms.r2.domain.dataItem.DataItemRepository;
import edu.njnu.opengms.r2.domain.model.ModelRepository;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstanceController;
import edu.njnu.opengms.r2.domain.scenario.ScenarioService;
import edu.njnu.opengms.r2.utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author ：Zhiyi
 * @Date ：2023/9/1 19:36
 * @modified By：
 * @version: 1.0.0
 */
@Service
public class AssessmentService {

    @Autowired
    AssessmentRepository assessmentRepository;

    @Autowired
    ModelRepository modelRepository;

    @Autowired
    DataItemRepository dataItemRepository;

    @Autowired
    AssessmentMethodRepository assessmentMethodRepository;

    @Autowired
    ScenarioService scenarioService;


    @Autowired
    FunctionUtils functionUtils;


    @Autowired
    ModelInstanceController modelInstanceController;
    public List<Assessment> get(String userId, String scenarioId) {
        List<Assessment> assessment = assessmentRepository.findByReproducedScenarioId(scenarioId);
        return  assessment;
    }

    public Assessment create(AddAssessmentDTO add, String userId) {
        Assessment assessment = new Assessment();
        add.convertTo(assessment);
        assessment.setCreatorId(userId);
//        assessment.setPurpose(purpose);
        Assessment newA = assessmentRepository.insert(assessment);
        return newA;
    }

    public Object startAssessment(JSONObject form) {
        return null;
    }

    public Object autoAssessment(String reproducedScenarioId) {
        JSONObject reproducedScenario= scenarioService.getScenario(reproducedScenarioId);
        JSONObject initialScenario= reproducedScenario.getJSONObject("initialScenarioObject");
        List<JSONObject> initialInstanceObjectList = (List<JSONObject>) initialScenario.get("instanceObjectList");
        List<JSONObject> reproducedInstanceObjectList = (List<JSONObject>) reproducedScenario.get("instanceObjectList");
        getFlow(initialInstanceObjectList);

        return null;
    }
    public Object getFlow(List<JSONObject> instanceObjectList){

        for (JSONObject instance : instanceObjectList){
            List<JSONObject> stateList = (List<JSONObject>) instance.get("behavior");
            for (JSONObject state:  stateList){
                List<JSONObject> inputEvents = (List<JSONObject>) state.get("inputs");
                Boolean existIntermediate = false ;
                for(JSONObject input:inputEvents){
                    Boolean isIntermediate =  input.getJSONObject("datasetItem").getBool("isIntermediate");
                    if(isIntermediate){
                        existIntermediate = true;
                    }
                }
                if(!existIntermediate){
                    //这个model是leveL1 的 操作
                }

                List<JSONObject> outputEvents = (List<JSONObject>) state.get("outputs");
                List<JSONObject> parameterEvents = (List<JSONObject>) state.get("parameters");

            }


        }
        return null;
    }


    public Object update(UpdateAssessmentDTO update, String userId, String id) {
        Assessment assessment = assessmentRepository.findById(id).orElseThrow(MyException::noObject);
        update.updateTo(assessment);
        return assessmentRepository.save( assessment);
    }
}
