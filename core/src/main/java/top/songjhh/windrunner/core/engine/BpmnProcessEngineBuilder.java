package top.songjhh.windrunner.core.engine;

import top.songjhh.windrunner.core.engine.deployment.DeploymentService;
import top.songjhh.windrunner.core.engine.deployment.DeploymentServiceImpl;
import top.songjhh.windrunner.core.engine.deployment.repository.DeploymentRepository;
import top.songjhh.windrunner.core.engine.deployment.repository.LocalDeploymentRepository;
import top.songjhh.windrunner.core.engine.executor.FlowExecutorFactory;
import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.identity.LocalIdentityServiceImpl;
import top.songjhh.windrunner.core.engine.listener.SetAssigneeToStarterTaskListener;
import top.songjhh.windrunner.core.engine.listener.TaskListener;
import top.songjhh.windrunner.core.engine.listener.TaskListenerFactory;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.process.ProcessServiceImpl;
import top.songjhh.windrunner.core.engine.process.repository.LocalProcessInstanceRepository;
import top.songjhh.windrunner.core.engine.process.repository.ProcessInstanceRepository;
import top.songjhh.windrunner.core.engine.runtime.RuntimeService;
import top.songjhh.windrunner.core.engine.runtime.RuntimeServiceImpl;
import top.songjhh.windrunner.core.engine.task.TaskService;
import top.songjhh.windrunner.core.engine.task.TaskServiceImpl;
import top.songjhh.windrunner.core.engine.task.repository.LocalTaskRepository;
import top.songjhh.windrunner.core.engine.task.repository.TaskRepository;

/**
 * @author songjhh
 */
public class BpmnProcessEngineBuilder {

    private DeploymentService deploymentService;
    private ProcessService processService;
    private TaskService taskService;
    private IdentityService identityService;

    private BpmnProcessEngineBuilder() {
    }

    public static BpmnProcessEngineBuilder builder() {
        TaskListenerFactory.register(new SetAssigneeToStarterTaskListener());
        return new BpmnProcessEngineBuilder();
    }

    public BpmnProcessEngineBuilder deploymentRepository(DeploymentRepository repository) {
        this.deploymentService = new DeploymentServiceImpl(repository);
        return this;
    }

    public BpmnProcessEngineBuilder processInstanceRepository(ProcessInstanceRepository repository) {
        this.processService = new ProcessServiceImpl(repository);
        return this;
    }

    public BpmnProcessEngineBuilder identityService(IdentityService identityService) {
        this.identityService = identityService;
        return this;
    }

    public BpmnProcessEngineBuilder taskRepository(TaskRepository repository) {
        this.taskService = new TaskServiceImpl(repository);
        return this;
    }

    public BpmnProcessEngineBuilder registerTaskListener(TaskListener listener) {
        TaskListenerFactory.register(listener);
        return this;
    }

    public BpmnProcessEngine build() {
        if (deploymentService == null) {
            this.deploymentService = new DeploymentServiceImpl(new LocalDeploymentRepository());
        }
        if (processService == null) {
            this.processService = new ProcessServiceImpl(new LocalProcessInstanceRepository());
        }
        if (taskService == null) {
            this.taskService = new TaskServiceImpl(new LocalTaskRepository());
        }
        if (identityService == null) {
            this.identityService = new LocalIdentityServiceImpl();
        }
        RuntimeService runtimeService =
                new RuntimeServiceImpl(deploymentService, processService, taskService, identityService);
        FlowExecutorFactory.create(processService, taskService, identityService);
        return new BpmnProcessEngine(deploymentService, processService, taskService, runtimeService, identityService);
    }

}