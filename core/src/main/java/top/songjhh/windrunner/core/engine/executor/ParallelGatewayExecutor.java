package top.songjhh.windrunner.core.engine.executor;

import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.ParallelGateway;
import top.songjhh.windrunner.core.engine.runtime.model.SequenceFlow;
import top.songjhh.windrunner.core.engine.task.TaskService;
import top.songjhh.windrunner.core.engine.task.model.Task;

import java.util.List;

/**
 * @author songjhh
 */
public class ParallelGatewayExecutor extends AbstractFlowNodeExecutor {

    public ParallelGatewayExecutor(ProcessService processService,
                                   TaskService taskService,
                                   IdentityService identityService) {
        super(processService, taskService, identityService);
    }

    @Override
    public boolean preExecute(RuntimeContext context, FlowElement executeElement, SequenceFlow incomingSequenceFlow) {
        ParallelGateway parallelGateway = (ParallelGateway) executeElement;
        if (this.checkExecutorNext(context, parallelGateway)) {
            return false;
        }
        this.executorNext(context, parallelGateway);
        ProcessInstance processInstance = context.getProcessInstance();
        processInstance.completeNode(parallelGateway.getId(), null);
        processService.save(processInstance);
        return true;
    }

    @Override
    public void doExecute(RuntimeContext context, FlowElement executeElement, Task task) {
        throw new UnsupportedOperationException();
    }

    private boolean checkExecutorNext(RuntimeContext context, ParallelGateway parallelGateway) {
        List<SequenceFlow> previousSequenceFlowList = context.findPreviousSequenceFlows(parallelGateway.getIncoming());
        for (SequenceFlow sequenceFlow : previousSequenceFlowList) {
            FlowElement previousFlowNode = context.findPreviousFlowNode(sequenceFlow);
            if (context.getProcessInstance().getCurrentNodeIds().contains(previousFlowNode.getId())) {
                return true;
            }
        }
        return false;
    }

    private void executorNext(RuntimeContext context, ParallelGateway parallelGateway) {
        List<SequenceFlow> nextSequenceFlowList = context.findNextSequenceFlows(parallelGateway.getOutgoing());
        for (SequenceFlow nextSequenceFlow : nextSequenceFlowList) {
            FlowElement nextFlowNode = context.findNextFlowNode(nextSequenceFlow);
            nextSequenceFlow.cleanCondition();
            FlowExecutorFactory.getExecutor(nextFlowNode.getType()).preExecute(context, nextFlowNode, nextSequenceFlow);
        }
    }
}
