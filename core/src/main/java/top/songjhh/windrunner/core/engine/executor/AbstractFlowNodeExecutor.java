package top.songjhh.windrunner.core.engine.executor;

import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.SequenceFlow;
import top.songjhh.windrunner.core.engine.task.TaskService;

import java.util.List;

/**
 * @author songjhh
 */
public abstract class AbstractFlowNodeExecutor implements FlowNodeExecutor {

    protected final ProcessService processService;
    protected final TaskService taskService;
    protected final IdentityService identityService;

    protected AbstractFlowNodeExecutor(ProcessService processService,
                                       TaskService taskService,
                                       IdentityService identityService) {
        this.processService = processService;
        this.taskService = taskService;
        this.identityService = identityService;
    }

    protected void executeNext(RuntimeContext context, List<String> outgoing) {
        List<SequenceFlow> nextSequenceFlowList = context.findNextSequenceFlows(outgoing);
        for (SequenceFlow sequenceFlow : nextSequenceFlowList) {
            FlowElement nextFlowNode = context.findNextFlowNode(sequenceFlow);
            FlowExecutorFactory.getExecutor(nextFlowNode.getType()).preExecute(context, nextFlowNode, sequenceFlow);
        }
    }

}
