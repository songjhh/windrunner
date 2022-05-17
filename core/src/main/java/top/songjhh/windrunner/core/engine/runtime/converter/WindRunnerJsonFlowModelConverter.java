package top.songjhh.windrunner.core.engine.runtime.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import top.songjhh.windrunner.core.engine.runtime.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author songjhh
 */
public class WindRunnerJsonFlowModelConverter implements FlowModelConverter {
    @Override
    public FlowModel convertToModel(String source) {
        FlowModel flowModel = new FlowModel();
        List<FlowElement> flowElementList = new ArrayList<>();

        Gson gson = new Gson();
        Map<String, Object> map = gson.fromJson(source, new TypeToken<Map<String, Object>>() {
        }.getType());
        List<Map<String, Object>> list = gson.fromJson(gson.toJson(map.get("flowElementList")),
                new TypeToken<List<Map<String, Object>>>() {
                }.getType());
        for (Map<String, Object> elementMap : list) {
            if (FlowElement.Type.START_EVENT.toString().equals(elementMap.get("type"))) {
                flowElementList.add(gson.fromJson(gson.toJson(elementMap), StartEvent.class));
            }
            if (FlowElement.Type.END_EVENT.toString().equals(elementMap.get("type"))) {
                flowElementList.add(gson.fromJson(gson.toJson(elementMap), EndEvent.class));
            }
            if (FlowElement.Type.SEQUENCE_FLOW.toString().equals(elementMap.get("type"))) {
                flowElementList.add(gson.fromJson(gson.toJson(elementMap), SequenceFlow.class));
            }
            if (FlowElement.Type.USER_TASK.toString().equals(elementMap.get("type"))) {
                flowElementList.add(gson.fromJson(gson.toJson(elementMap), UserTask.class));
            }
        }
        flowModel.setFlowElementList(flowElementList);
        return flowModel;
    }
}
