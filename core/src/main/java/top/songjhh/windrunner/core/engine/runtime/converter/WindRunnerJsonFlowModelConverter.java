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
public class WindRunnerJsonFlowModelConverter {

    private WindRunnerJsonFlowModelConverter() {
    }

    public static final String TYPE = "type";
    public static final String FLOW_ELEMENT_LIST = "flowElementList";

    public static FlowModel convertToModel(String source) {
        Gson gson = new Gson();
        Map<String, Object> map = sourceToMap(source, gson);
        List<Map<String, Object>> list = mapToFlowElementList(map, gson);
        return flowElementListToModel(list, gson);
    }

    private static FlowModel flowElementListToModel(List<Map<String, Object>> list, Gson gson) {
        FlowModel flowModel = new FlowModel();
        List<FlowElement> flowElementList = new ArrayList<>();
        for (Map<String, Object> elementMap : list) {
            String json = gson.toJson(elementMap);
            if (FlowElement.Type.START_EVENT.toString().equals(elementMap.get(TYPE))) {
                flowElementList.add(gson.fromJson(json, StartEvent.class));
            }
            if (FlowElement.Type.END_EVENT.toString().equals(elementMap.get(TYPE))) {
                flowElementList.add(gson.fromJson(json, EndEvent.class));
            }
            if (FlowElement.Type.SEQUENCE_FLOW.toString().equals(elementMap.get(TYPE))) {
                flowElementList.add(gson.fromJson(json, SequenceFlow.class));
            }
            if (FlowElement.Type.USER_TASK.toString().equals(elementMap.get(TYPE))) {
                flowElementList.add(gson.fromJson(json, UserTask.class));
            }
            if (FlowElement.Type.EXCLUSIVE_GATEWAY.toString().equals(elementMap.get(TYPE))) {
                flowElementList.add(gson.fromJson(json, ExclusiveGateway.class));
            }
            if (FlowElement.Type.PARALLEL_GATEWAY.toString().equals(elementMap.get(TYPE))) {
                flowElementList.add(gson.fromJson(json, ParallelGateway.class));
            }
        }
        flowModel.setFlowElementList(flowElementList);
        return flowModel;
    }

    private static List<Map<String, Object>> mapToFlowElementList(Map<String, Object> map, Gson gson) {
        return gson.fromJson(gson.toJson(map.get(FLOW_ELEMENT_LIST)),
                new TypeToken<List<Map<String, Object>>>() {
                }.getType());
    }

    private static Map<String, Object> sourceToMap(String source, Gson gson) {
        return gson.fromJson(source, new TypeToken<Map<String, Object>>() {
        }.getType());
    }
}
