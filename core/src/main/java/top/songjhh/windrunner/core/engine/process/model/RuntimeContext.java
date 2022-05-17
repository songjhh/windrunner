package top.songjhh.windrunner.core.engine.process.model;

import lombok.Getter;
import top.songjhh.windrunner.core.engine.executor.FlowExecutorFactory;
import top.songjhh.windrunner.core.engine.runtime.converter.FlowModelConvertProvider;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.FlowModel;
import top.songjhh.windrunner.core.engine.runtime.model.SequenceFlow;
import top.songjhh.windrunner.core.engine.runtime.model.StartEvent;
import top.songjhh.windrunner.core.engine.task.model.Task;
import top.songjhh.windrunner.core.exception.FlowElementCastException;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by @author songjhh
 */
@Getter
public class RuntimeContext {

    private final Map<String, FlowElement> flowElementMap;
    private final ProcessInstance processInstance;
    private final List<String> currentNodeNames;

    private RuntimeContext(ProcessInstance processInstance) {
        FlowModel model = FlowModelConvertProvider.converterToModel(processInstance.getSource(),
                processInstance.getType());
        this.flowElementMap = model.getFlowElementList().stream()
                .collect(Collectors.toMap(FlowElement::getId, Function.identity()));
        this.processInstance = processInstance;
        this.currentNodeNames = processInstance.getCurrentNodeIds().stream()
                .map(flowElementMap::get).map(FlowElement::getName).collect(Collectors.toList());
    }

    public static RuntimeContext getContextByInstance(ProcessInstance processInstance) {
        return new RuntimeContext(processInstance);
    }

    public RuntimeContext startProcess() {
        StartEvent startEvent = findStartEvent();
        FlowExecutorFactory.getExecutor(startEvent.getType()).doExecute(this, startEvent, null);
        return this;
    }

    public RuntimeContext commit(Task task) {
        FlowElement flowElement = flowElementMap.get(task.getNodeId());
        FlowExecutorFactory.getExecutor(flowElement.getType()).doExecute(this, flowElement, task);
        return this;
    }

    public StartEvent findStartEvent() {
        for (Map.Entry<String, FlowElement> entry : flowElementMap.entrySet()) {
            if (FlowElement.Type.START_EVENT.equals(entry.getValue().getType())) {
                if (!(entry.getValue() instanceof StartEvent)) {
                    throw new FlowElementCastException(StartEvent.class);
                }
                return (StartEvent) entry.getValue();
            }
        }
        return null;
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

    private List<SequenceFlow> getSequenceFlowsByDirection(List<String> ids) {
        return flowElementMap.entrySet().stream()
                .filter(it -> ids.contains(it.getKey())).map(Map.Entry::getValue)
                .map(SequenceFlow.class::cast).collect(Collectors.toList());
    }

    public void updateVariables(Map<String, Object> variables) {
        if (variables != null) {
            this.processInstance.putAllVariables(variables);
        }
    }

}
