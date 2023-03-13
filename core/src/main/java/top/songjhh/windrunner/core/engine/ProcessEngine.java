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

    /**
     * 获取部署service
     *
     * @return service
     */
    DeploymentService getDeploymentService();

    /**
     * 获取部署进程
     *
     * @return service
     */
    ProcessService getProcessService();

    /**
     * 获取部署任务
     *
     * @return service
     */
    TaskService getTaskService();

    /**
     * 获取部署认证
     *
     * @return service
     */
    IdentityService getIdentityService();

    /**
     * 获取部署进行时
     *
     * @return service
     */
    RuntimeService getRuntimeService();

}
