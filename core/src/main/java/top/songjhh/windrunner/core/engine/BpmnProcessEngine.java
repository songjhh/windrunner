package top.songjhh.windrunner.core.engine;

import top.songjhh.windrunner.core.engine.deployment.DeploymentService;
import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.runtime.RuntimeService;
import top.songjhh.windrunner.core.engine.task.TaskService;

/**
 * Created by @author songjhh
 */
public class BpmnProcessEngine implements ProcessEngine {

    private final DeploymentService deploymentService;
    private final ProcessService processService;
    private final TaskService taskService;
    private final RuntimeService runtimeService;
    private final IdentityService identityService;

    public BpmnProcessEngine(DeploymentService deploymentService, ProcessService processService,
                             TaskService taskService, RuntimeService runtimeService, IdentityService identityService) {
        this.deploymentService = deploymentService;
        this.processService = processService;
        this.taskService = taskService;
        this.runtimeService = runtimeService;
        this.identityService = identityService;
    }

    @Override
    public DeploymentService getDeploymentService() {
        return this.deploymentService;
    }

    @Override
    public ProcessService getProcessService() {
        return this.processService;
    }

    @Override
    public TaskService getTaskService() {
        return this.taskService;
    }

    @Override
    public RuntimeService getRuntimeService() {
        return this.runtimeService;
    }
}
