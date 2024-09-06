package edu.njnu.opengms.r2.domain.assessment;

import cn.hutool.json.JSONObject;
import edu.njnu.opengms.common.exception.MyException;
import edu.njnu.opengms.r2.domain.AssessmentMethod.AssessmentMethod;
import edu.njnu.opengms.r2.domain.AssessmentMethod.AssessmentMethodRepository;
import edu.njnu.opengms.r2.domain.assessment.dto.AddAssessmentDTO;
import edu.njnu.opengms.r2.domain.dataItem.DataItem;
import edu.njnu.opengms.r2.domain.dataItem.DataItemRepository;
import edu.njnu.opengms.r2.domain.model.Model;
import edu.njnu.opengms.r2.domain.model.ModelRepository;
import edu.njnu.opengms.r2.domain.modelInstance.ModelInstanceController;
import edu.njnu.opengms.r2.domain.scenario.ScenarioService;
import edu.njnu.opengms.r2.utils.FunctionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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

    public Assessment create(AddAssessmentDTO add, String userId) {
        Assessment assessment = new Assessment();
        add.convertTo(assessment);
        assessment.setCreatorId(userId);
        Assessment newA = assessmentRepository.insert(assessment);
        return newA;
    }


    public Object get(String assessmentId) {
        Assessment assessment = assessmentRepository.findById(assessmentId).orElseThrow(MyException::noObject);
        return getAssessmentObject(assessment);
    }

    public Object startAssessment(JSONObject form) {
        return null;
    }

    public Object autoAssessment(String reproducedScenarioId) {
        JSONObject reproducedScenario = scenarioService.getScenario(reproducedScenarioId);
        JSONObject initialScenario = reproducedScenario.getJSONObject("initialScenarioObject");
        List<JSONObject> initialInstanceObjectList = (List<JSONObject>) initialScenario.get("instanceObjectList");
        List<JSONObject> reproducedInstanceObjectList = (List<JSONObject>) reproducedScenario.get("instanceObjectList");
        getFlow(initialInstanceObjectList);

        return null;
    }

    public Object getFlow(List<JSONObject> instanceObjectList) {

        for (JSONObject instance : instanceObjectList) {
            List<JSONObject> stateList = (List<JSONObject>) instance.get("behavior");
            for (JSONObject state : stateList) {
                List<JSONObject> inputEvents = (List<JSONObject>) state.get("inputs");
                Boolean existIntermediate = false;
                for (JSONObject input : inputEvents) {
                    Boolean isIntermediate = input.getJSONObject("datasetItem").getBool("isIntermediate");
                    if (isIntermediate) {
                        existIntermediate = true;
                    }
                }
                if (!existIntermediate) {
                    //这个model是leveL1 的 操作
                }

                List<JSONObject> outputEvents = (List<JSONObject>) state.get("outputs");
                List<JSONObject> parameterEvents = (List<JSONObject>) state.get("parameters");


            }


        }
        return null;
    }

    JSONObject getAssessmentObject(Assessment assessment) {
        JSONObject obj = new JSONObject();
        JSONObject newAssessment = new JSONObject();
        String initialResourceId = assessment.getInitialResourceId();
        String reproducedResourceId = assessment.getReproducedResourceId();

        if (assessment.getObject().equals("model")) {
            Optional<Model> initialResourceObject = modelRepository.findById(initialResourceId);
            Optional<Model> reproducedResourceObject = modelRepository.findById(reproducedResourceId);
            newAssessment.put("initialResourceObject", initialResourceObject);
            newAssessment.put("reproducedResourceObject", reproducedResourceObject);
        }
        if (assessment.getObject().equals("data")) {
            Optional<DataItem> initialResourceObject = dataItemRepository.findById(initialResourceId);
            Optional<DataItem> reproducedResourceObject = dataItemRepository.findById(reproducedResourceId);
            newAssessment.put("initialResourceObject", initialResourceObject);
            newAssessment.put("reproducedResourceObject", reproducedResourceObject);
        }
        Optional<AssessmentMethod> assessmentMethod = assessmentMethodRepository.findById(assessment.getComparasionMethodId());
        newAssessment.put("comparasionMethodObject", assessmentMethod);
        newAssessment.put("object", assessment.object);
        newAssessment.put("creatorId", assessment.creatorId);
        newAssessment.put("projectId", assessment.projectId);
        newAssessment.put("id", assessment.id);
        newAssessment.put("initialScenarioId", assessment.initialScenarioId);
        newAssessment.put("reproducedScenarioId", assessment.reproducedScenarioId);
        newAssessment.put("purpose", assessment.purpose);

        return newAssessment;


    }


}
