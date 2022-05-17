package top.songjhh.windrunner.core.engine.runtime;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.deployment.DeploymentService;
import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.task.TaskService;
import top.songjhh.windrunner.core.engine.task.model.Task;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author songjhh
 */
public class RuntimeServiceImpl implements RuntimeService {

    private final DeploymentService deploymentService;
    private final ProcessService processService;
    private final TaskService taskService;
    private final IdentityService identityService;

    public RuntimeServiceImpl(DeploymentService deploymentService,
                              ProcessService processService,
                              TaskService taskService,
                              IdentityService identityService) {
        this.deploymentService = deploymentService;
        this.processService = processService;
        this.taskService = taskService;
        this.identityService = identityService;
    }

    @Override
    public RuntimeContext startProcessByDeploymentId(String starter,
                                                     String deploymentId,
                                                     Map<String, Object> variables) {
        Deployment deployment = deploymentService.getDeploymentById(deploymentId);
        ProcessInstance processInstance = processService.startProcessByDeployment(
                identityService.getEntityByUserId(starter), deployment, variables);
        RuntimeContext runtimeContext = RuntimeContext.getContextByInstance(processInstance);
        return runtimeContext.startProcess();
    }

    @Override
    public RuntimeContext commit(String assignee, String taskId, Map<String, Object> variables) {
        Task task = taskService.getById(taskId);
        ProcessInstance processInstance = processService.getInstanceById(task.getInstanceId());
        RuntimeContext runtimeContext = RuntimeContext.getContextByInstance(processInstance);
        return runtimeContext.commit(task.complete(identityService.getEntityByUserId(assignee), variables));
    }

    @Override
    public RuntimeContext save(String taskId, Map<String, Object> variables) {
        Task task = taskService.getById(taskId);
        ProcessInstance processInstance = processService.getInstanceById(task.getInstanceId());
        RuntimeContext runtimeContext = RuntimeContext.getContextByInstance(processInstance);
        runtimeContext.updateVariables(variables);
        taskService.save(task);
        processService.save(runtimeContext.getProcessInstance());
        return runtimeContext;
    }

    @Override
    public <T extends AdvancedPagedQuery> List<RuntimeContext> list(T query) {
        return processService.list(query).stream().map(RuntimeContext::getContextByInstance).collect(Collectors.toList());
    }

    @Override
    public RuntimeContext getByInstanceId(String instanceId) {
        return RuntimeContext.getContextByInstance(processService.getInstanceById(instanceId));
    }
}
