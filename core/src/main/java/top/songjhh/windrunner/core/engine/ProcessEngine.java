package top.songjhh.windrunner.core.engine;

import top.songjhh.windrunner.core.engine.deployment.DeploymentService;
import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.runtime.RuntimeService;
import top.songjhh.windrunner.core.engine.task.TaskService;

/**
 * Created by @author songjhh
 */
public interface ProcessEngine {

    DeploymentService getDeploymentService();

    ProcessService getProcessService();

    TaskService getTaskService();

    IdentityService getIdentityService();

    RuntimeService getRuntimeService();

}
