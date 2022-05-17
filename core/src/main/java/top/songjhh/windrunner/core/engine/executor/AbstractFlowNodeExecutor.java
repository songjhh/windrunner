package top.songjhh.windrunner.core.engine.executor;

import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.task.TaskService;

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
}
