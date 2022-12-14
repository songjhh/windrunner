package top.songjhh.windrunner.core.engine.runtime;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.deployment.DeploymentService;
import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.process.model.ProcessStatus;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.FlowNode;
import top.songjhh.windrunner.core.engine.runtime.model.SequenceFlow;
import top.songjhh.windrunner.core.engine.runtime.model.UserTask;
import top.songjhh.windrunner.core.engine.task.TaskService;
import top.songjhh.windrunner.core.engine.task.model.Task;
import top.songjhh.windrunner.core.exception.DeploymentNotDeployException;
import top.songjhh.windrunner.core.exception.ProcessInstanceNotDraftException;
import top.songjhh.windrunner.core.exception.UserTaskTakeBackException;

import java.util.*;
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
        if (!Deployment.Status.DEPLOYED.equals(deployment.getStatus())) {
            throw new DeploymentNotDeployException();
        }
        ProcessInstance processInstance = processService.startProcessByDeployment(
                identityService.getEntityByUserId(starter), deployment, variables);
        RuntimeContext runtimeContext = RuntimeContext.getContextByInstance(processInstance, deployment);
        return runtimeContext.startProcess();
    }

    @Override
    public RuntimeContext draftProcessByDeploymentId(String starter, String deploymentId,
                                                     Map<String, Object> variables) {
        Deployment deployment = deploymentService.getDeploymentById(deploymentId);
        if (!Deployment.Status.DEPLOYED.equals(deployment.getStatus())) {
            throw new DeploymentNotDeployException();
        }
        ProcessInstance processInstance = processService.draftProcessByDeployment(
                identityService.getEntityByUserId(starter), deployment, variables);
        return RuntimeContext.getContextByInstance(processInstance, deployment);
    }

    @Override
    public RuntimeContext saveDraft(String instanceId, Map<String, Object> variables) {
        ProcessInstance processInstance = processService.getInstanceById(instanceId);
        if (processInstance == null || !ProcessStatus.DRAFT.equals(processInstance.getStatus())) {
            throw new ProcessInstanceNotDraftException();
        }
        processInstance.setVariables(variables);
        processService.save(processInstance);
        Deployment deployment = deploymentService.getDeploymentById(processInstance.getDeploymentId());
        return RuntimeContext.getContextByInstance(processInstance, deployment);
    }

    @Override
    public RuntimeContext startProcessByDraft(String instanceId, Map<String, Object> variables) {
        ProcessInstance processInstance = processService.startProcessByDraft(instanceId, variables);
        Deployment deployment = deploymentService.getDeploymentById(processInstance.getDeploymentId());
        RuntimeContext runtimeContext = RuntimeContext.getContextByInstance(processInstance, deployment);
        return runtimeContext.startProcess();
    }

    @Override
    public RuntimeContext commit(String assignee, String taskId, Map<String, Object> variables) {
        Task task = taskService.getById(taskId);
        ProcessInstance processInstance = processService.getInstanceById(task.getInstanceId());
        Deployment deployment = deploymentService.getDeploymentById(processInstance.getDeploymentId());
        RuntimeContext runtimeContext = RuntimeContext.getContextByInstance(processInstance, deployment);
        return runtimeContext.commit(task.complete(identityService.getEntityByUserId(assignee), variables));
    }

    @Override
    public RuntimeContext save(String taskId, Map<String, Object> variables) {
        Task task = taskService.getById(taskId);
        task.setVariables(variables);
        ProcessInstance processInstance = processService.getInstanceById(task.getInstanceId());
        Deployment deployment = deploymentService.getDeploymentById(processInstance.getDeploymentId());
        taskService.save(task);
        return RuntimeContext.getContextByInstance(processInstance, deployment);
    }

    @Override
    public boolean canTakeBack(String taskId) {
        Task task = taskService.getById(taskId);
        ProcessInstance processInstance = processService.getInstanceById(task.getInstanceId());
        Deployment deployment = deploymentService.getDeploymentById(processInstance.getDeploymentId());
        RuntimeContext runtimeContext = RuntimeContext.getContextByInstance(processInstance, deployment);
        UserTask userTask = (UserTask) runtimeContext.findFlowNode(task.getNodeId());
        return this.checkTakeBack(task, userTask, runtimeContext);
    }

    @Override
    public RuntimeContext takeBack(String taskId) {
        Task task = taskService.getById(taskId);
        ProcessInstance processInstance = processService.getInstanceById(task.getInstanceId());
        Deployment deployment = deploymentService.getDeploymentById(processInstance.getDeploymentId());
        RuntimeContext runtimeContext = RuntimeContext.getContextByInstance(processInstance, deployment);
        UserTask userTask = (UserTask) runtimeContext.findFlowNode(task.getNodeId());

        if (!this.checkTakeBack(task, userTask, runtimeContext)) {
            throw new UserTaskTakeBackException();
        }
        // ????????????????????????????????????
        List<Task> nextFlowNodeTasks = this.getNextFlowNodeTasks(userTask, runtimeContext);
        for (Task it : nextFlowNodeTasks) {
            // ???????????????????????????
            it.terminate();
            taskService.save(it);
            // ????????????
            processInstance.goBackNode(it.getNodeId());
        }
        processInstance.reopenNode(userTask.getId());

        // ??????????????????
        userTask.setAssignee(task.getAssignee());
        userTask.setAssigneeName(task.getAssigneeName());
        Task newTask = Task.create(processInstance, userTask);
        taskService.save(newTask);
        // ????????????????????????
        task.takeBack();
        taskService.save(task);

        processInstance.runNode(userTask.getId());
        processService.save(processInstance);

        processInstance = processService.getInstanceById(task.getInstanceId());
        return RuntimeContext.getContextByInstance(processInstance, deployment);
    }

    private void setNextFlowNodeIds(Set<String> flowElementIds, FlowNode flowNode, RuntimeContext runtimeContext) {
        // ????????????????????????
        List<SequenceFlow> nextSequenceFlows = runtimeContext.findNextSequenceFlows(flowNode.getOutgoing());
        for (SequenceFlow sequenceFlow : nextSequenceFlows) {
            FlowElement flowElement = runtimeContext.findNextFlowNode(sequenceFlow);
            // ?????????????????????????????????
            if (FlowElement.Type.PARALLEL_GATEWAY.equals(flowElement.getType())
                    || FlowElement.Type.EXCLUSIVE_GATEWAY.equals(flowElement.getType())) {
                setNextFlowNodeIds(flowElementIds, (FlowNode) flowElement, runtimeContext);
                continue;
            }
            flowElementIds.add(flowElement.getId());
        }
    }

    @Override
    public <T extends AdvancedPagedQuery> List<RuntimeContext> list(T query) {
        return processService.list(query).stream()
                .map(it -> RuntimeContext.getContextByInstance(
                        it, deploymentService.getDeploymentById(it.getDeploymentId())))
                .collect(Collectors.toList());
    }

    @Override
    public RuntimeContext getByInstanceId(String instanceId) {
        ProcessInstance processInstance = processService.getInstanceById(instanceId);
        Deployment deployment = deploymentService.getDeploymentById(processInstance.getDeploymentId());
        return RuntimeContext.getContextByInstance(processInstance, deployment);
    }

    private boolean checkTakeBack(Task task, UserTask userTask, RuntimeContext runtimeContext) {
        if (!Task.Status.FINISH.equals(task.getStatus())
                || !ProcessStatus.RUNNING.equals(runtimeContext.getProcessInstance().getStatus())) {
            return false;
        }
        List<Task> nextFlowNodeTasks = getNextFlowNodeTasks(userTask, runtimeContext);
        for (Task it : nextFlowNodeTasks) {
            boolean cannotTaskBack = Boolean.TRUE.equals(it.getRead()) || Task.Status.FINISH.equals(it.getStatus());
            if (cannotTaskBack) {
                return false;
            }
        }
        return true;
    }

    private List<Task> getNextFlowNodeTasks(UserTask userTask, RuntimeContext runtimeContext) {
        // ????????????????????????????????????
        Set<String> nextFlowNodeIds = new HashSet<>();
        setNextFlowNodeIds(nextFlowNodeIds, userTask, runtimeContext);
        // ?????????????????????????????????????????????????????????????????????
        List<Task> nextFlowNodeTasks = new ArrayList<>();
        for (String nextFlowNodeId : nextFlowNodeIds) {
            nextFlowNodeTasks.addAll(taskService.listTasksByInstanceIdAndNodeId(
                    runtimeContext.getProcessInstance().getInstanceId(), nextFlowNodeId));
        }
        return nextFlowNodeTasks;
    }
}
