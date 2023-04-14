package top.songjhh.windrunner.core.engine.executor;

import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.EndEvent;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.SequenceFlow;
import top.songjhh.windrunner.core.engine.task.TaskService;
import top.songjhh.windrunner.core.engine.task.model.Task;

/**
 * @author songjhh
 */
public class EndEventExecutor extends AbstractFlowNodeExecutor {

    protected EndEventExecutor(ProcessService processService, TaskService taskService,
                               IdentityService identityService) {
        super(processService, taskService, identityService);
    }

    @Override
    public boolean preExecute(RuntimeContext context, FlowElement executeElement, SequenceFlow incomingSequenceFlow) {
        EndEvent endEvent = (EndEvent) executeElement;
        ProcessInstance processInstance = context.getProcessInstance();
        taskService.listTasksByInstanceId(processInstance.getInstanceId())
                .stream()
                .filter(it -> Task.Status.PROCESSING.equals(it.getStatus()))
                .forEach(it -> taskService.save(it.terminate()));
        processInstance.completeNode(endEvent.getId(), null);
        processInstance.complete();
        processInstance.walkedEdge(incomingSequenceFlow.getId());
        processService.save(processInstance);
        return true;
    }

    @Override
    public void doExecute(RuntimeContext context, FlowElement executeElement, Task task) {
        throw new UnsupportedOperationException();
    }
}
