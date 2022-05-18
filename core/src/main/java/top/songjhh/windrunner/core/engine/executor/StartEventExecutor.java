package top.songjhh.windrunner.core.engine.executor;

import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.SequenceFlow;
import top.songjhh.windrunner.core.engine.runtime.model.StartEvent;
import top.songjhh.windrunner.core.engine.task.TaskService;
import top.songjhh.windrunner.core.engine.task.model.Task;

/**
 * @author songjhh
 */
public class StartEventExecutor extends AbstractFlowNodeExecutor {

    public StartEventExecutor(ProcessService processService, TaskService taskService, IdentityService identityService) {
        super(processService, taskService, identityService);
    }

    @Override
    public boolean preExecute(RuntimeContext context, FlowElement executeElement, SequenceFlow incomingSequenceFlow) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void doExecute(RuntimeContext context, FlowElement executeElement, Task task) {
        StartEvent startEvent = (StartEvent) executeElement;
        executeNext(context, startEvent.getOutgoing());
    }
}
