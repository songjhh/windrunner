package top.songjhh.windrunner.core.engine.executor;

import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.ExclusiveGateway;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.SequenceFlow;
import top.songjhh.windrunner.core.engine.task.TaskService;
import top.songjhh.windrunner.core.engine.task.model.Task;

import java.util.List;

/**
 * @author songjhh
 */
public class ExclusiveGatewayExecutor extends AbstractFlowNodeExecutor {

    public ExclusiveGatewayExecutor(ProcessService processService,
                                    TaskService taskService,
                                    IdentityService identityService) {
        super(processService, taskService, identityService);
    }

    @Override
    public boolean preExecute(RuntimeContext context, FlowElement executeElement, SequenceFlow incomingSequenceFlow) {
        ExclusiveGateway exclusiveGateway = (ExclusiveGateway) executeElement;
        List<SequenceFlow> nextSequenceFlowList = context.findNextSequenceFlows(exclusiveGateway.getOutgoing());
        for (SequenceFlow nextSequenceFlow : nextSequenceFlowList) {
            FlowElement nextFlowNode = context.findNextFlowNode(nextSequenceFlow);
            boolean accept = FlowExecutorFactory.getExecutor(nextFlowNode.getType())
                    .preExecute(context, nextFlowNode, nextSequenceFlow);
            if (accept) {
                break;
            }
        }
        return true;
    }

    @Override
    public void doExecute(RuntimeContext context, FlowElement executeElement, Task task) {
        throw new UnsupportedOperationException();
    }
}
