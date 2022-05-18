package top.songjhh.windrunner.core.engine.process.model;

import lombok.Getter;
import top.songjhh.windrunner.core.engine.executor.FlowExecutorFactory;
import top.songjhh.windrunner.core.engine.runtime.converter.FlowModelConvertProvider;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.FlowModel;
import top.songjhh.windrunner.core.engine.runtime.model.SequenceFlow;
import top.songjhh.windrunner.core.engine.runtime.model.StartEvent;
import top.songjhh.windrunner.core.engine.task.model.Task;
import top.songjhh.windrunner.core.util.FlowElementUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by @author songjhh
 */
public class RuntimeContext {

    private final Map<String, FlowElement> flowElementMap;
    @Getter
    private final ProcessInstance processInstance;

    private RuntimeContext(ProcessInstance processInstance) {
        FlowModel model = FlowModelConvertProvider
                .converterToModel(processInstance.getSource(), processInstance.getType());
        this.flowElementMap = model.getFlowElementList().stream()
                .collect(Collectors.toMap(FlowElement::getId, Function.identity()));
        this.processInstance = processInstance;
    }

    public static RuntimeContext getContextByInstance(ProcessInstance processInstance) {
        return new RuntimeContext(processInstance);
    }

    public RuntimeContext startProcess() {
        StartEvent startEvent = FlowElementUtils.getStartEvent(processInstance.getSource(), processInstance.getType());
        FlowExecutorFactory.getExecutor(startEvent.getType()).doExecute(this, startEvent, null);
        return this;
    }

    public RuntimeContext commit(Task task) {
        FlowElement flowElement = flowElementMap.get(task.getNodeId());
        FlowExecutorFactory.getExecutor(flowElement.getType()).doExecute(this, flowElement, task);
        return this;
    }

    public List<SequenceFlow> findNextSequenceFlows(List<String> outgoing) {
        return getSequenceFlowsByDirection(outgoing);
    }

    public List<SequenceFlow> findPreviousSequenceFlows(List<String> incoming) {
        return getSequenceFlowsByDirection(incoming);
    }

    public FlowElement findNextFlowNode(SequenceFlow sequenceFlow) {
        return flowElementMap.get(sequenceFlow.getTargetRef());
    }

    public FlowElement findPreviousFlowNode(SequenceFlow sequenceFlow) {
        return flowElementMap.get(sequenceFlow.getSourceRef());
    }

    public void updateVariables(Map<String, Object> variables) {
        this.processInstance.putAllVariables(variables);
    }

    private List<SequenceFlow> getSequenceFlowsByDirection(List<String> ids) {
        return flowElementMap.entrySet().stream()
                .filter(it -> ids.contains(it.getKey())).map(Map.Entry::getValue)
                .map(SequenceFlow.class::cast).collect(Collectors.toList());
    }

    public List<String> getCurrentNodeNames() {
        return processInstance.getCurrentNodeIds().stream()
                .map(flowElementMap::get).map(FlowElement::getName).collect(Collectors.toList());
    }
}
