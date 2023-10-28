package top.songjhh.windrunner.core.engine.runtime;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.deployment.DeploymentService;
import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.process.model.ProcessStatus;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.*;
import top.songjhh.windrunner.core.engine.task.TaskService;
import top.songjhh.windrunner.core.engine.task.model.Task;
import top.songjhh.windrunner.core.exception.*;

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
        Deployment deployment =
                deploymentService.getDeploymentById(processService.getInstanceById(instanceId).getDeploymentId());
        if (!Deployment.Status.DEPLOYED.equals(deployment.getStatus())) {
            throw new DeploymentNotDeployException();
        }
        ProcessInstance processInstance = processService.startProcessByDraft(instanceId, variables);
        RuntimeContext runtimeContext = RuntimeContext.getContextByInstance(processInstance, deployment);
        return runtimeContext.startProcess();
    }

    @Override
    public RuntimeContext commit(String assignee, String taskId, Map<String, Object> variables) {
        Task task = taskService.getById(taskId);
        ProcessInstance processInstance = processService.getInstanceById(task.getInstanceId());
        if (!Task.Status.PROCESSING.equals(task.getStatus())) {
            throw new ProcessEngineException("该任务已完成/终止，请勿重复提交");
        }
        if (ProcessStatus.COMPLETED.equals(processInstance.getStatus()) || ProcessStatus.TERMINATED.equals(processInstance.getStatus())) {
            task.terminate();
            taskService.save(task);
            throw new ProcessEngineException("该流程已完成/终止，任务将标记终止");
        }
        if (!processInstance.getCurrentNodeIds().contains(task.getNodeId())) {
            task.terminate();
            taskService.save(task);
            throw new ProcessEngineException("流程出现异常，任务将标记终止");
        }
        Deployment deployment = deploymentService.getDeploymentById(processInstance.getDeploymentId());
        RuntimeContext runtimeContext = RuntimeContext.getContextByInstance(processInstance, deployment);
        return runtimeContext.commit(task.complete(identityService.getEntityByUserId(assignee), variables));
    }

    @Override
    public RuntimeContext reject(String assignee, String taskId, String rejectMessage) {
        Task task = taskService.getById(taskId);
        ProcessInstance processInstance = processService.getInstanceById(task.getInstanceId());
        Deployment deployment = deploymentService.getDeploymentById(processInstance.getDeploymentId());
        RuntimeContext runtimeContext = RuntimeContext.getContextByInstance(processInstance, deployment);
        UserTask userTask = (UserTask) runtimeContext.findFlowNode(task.getNodeId());
        UserEntity assigneeEntity = identityService.getEntityByUserId(assignee);

        UserTask previousUserTask = getPreviousUserTask(task, runtimeContext, userTask);

        task.reject(assigneeEntity, rejectMessage);
        taskService.save(task);

        processInstance.goBackNode(task.getNodeId());
        processInstance.reopenNode(previousUserTask.getId());
        List<Task> previousTasks = taskService.listTasksByInstanceIdAndNodeId(processInstance.getInstanceId(),
                previousUserTask.getId());
        previousTasks =
                previousTasks.stream().sorted(Comparator.comparing(Task::getEndDateTime).reversed()).collect(Collectors.toList());
        for (Task previousTask : previousTasks) {
            if (previousTask.getStatus().equals(Task.Status.FINISH)) {
                previousUserTask.setAssignee(previousTask.getAssignee());
                previousUserTask.setAssigneeName(previousTask.getAssigneeName());
                Task newTask = Task.create(processInstance, previousUserTask);
                taskService.save(newTask);
                break;
            }
        }
        processInstance.runNode(previousUserTask.getId());
        processService.save(processInstance);

        processInstance = processService.getInstanceById(task.getInstanceId());
        return RuntimeContext.getContextByInstance(processInstance, deployment);
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
        // 获取任务节点的所有子节点
        List<Task> nextFlowNodeTasks = this.getNextFlowNodeTasks(userTask, runtimeContext);
        for (Task it : nextFlowNodeTasks) {
            // 子节点所有任务废弃
            it.terminate();
            taskService.save(it);
            // 回退节点
            processInstance.goBackNode(it.getNodeId());
        }
        processInstance.reopenNode(userTask.getId());

        // 重新生成任务
        userTask.setAssignee(task.getAssignee());
        userTask.setAssigneeName(task.getAssigneeName());
        Task newTask = Task.create(processInstance, userTask);
        taskService.save(newTask);
        // 旧任务设置为拿回
        task = task.takeBack();
        taskService.save(task);

        processInstance.runNode(userTask.getId());
        processService.save(processInstance);

        processInstance = processService.getInstanceById(task.getInstanceId());
        return RuntimeContext.getContextByInstance(processInstance, deployment);
    }

    @Override
    public RuntimeContext transfer(String taskId, String fromUserId, String toUserId) {
        Task task = taskService.getById(taskId);
        if (!Task.Status.PROCESSING.equals(task.getStatus())) {
            throw new UserTaskTransferException("only processing task can transfer");
        }
        ProcessInstance processInstance = processService.getInstanceById(task.getInstanceId());
        Deployment deployment = deploymentService.getDeploymentById(processInstance.getDeploymentId());

        UserEntity fromUserEntity = identityService.getEntityByUserId(fromUserId);
        UserEntity toUserEntity = identityService.getEntityByUserId(toUserId);
        if (toUserEntity == null) {
            throw new UserTaskTransferException("can not find toUser");
        }

        Task newTask = task.copy();
        newTask.transfer(fromUserEntity, toUserEntity);
        taskService.save(newTask);

        task.transfered(fromUserEntity);
        taskService.save(task);
        return RuntimeContext.getContextByInstance(processInstance, deployment);
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
        // 获取任务节点的所有子节点
        Set<String> nextFlowNodeIds = new HashSet<>();
        setNextFlowNodeIds(nextFlowNodeIds, userTask, runtimeContext);
        // 如果子节点的任务，有已完成或查看，则不允许拿回
        List<Task> nextFlowNodeTasks = new ArrayList<>();
        for (String nextFlowNodeId : nextFlowNodeIds) {
            nextFlowNodeTasks.addAll(taskService.listTasksByInstanceIdAndNodeId(
                    runtimeContext.getProcessInstance().getInstanceId(), nextFlowNodeId));
        }
        return nextFlowNodeTasks;
    }

    private void setNextFlowNodeIds(Set<String> flowElementIds, FlowNode flowNode, RuntimeContext runtimeContext) {
        // 获取任务节点的线
        List<SequenceFlow> nextSequenceFlows = runtimeContext.findNextSequenceFlows(flowNode.getOutgoing());
        for (SequenceFlow sequenceFlow : nextSequenceFlows) {
            FlowElement flowElement = runtimeContext.findNextFlowNode(sequenceFlow);
            // 如果是网关则继续往下找
            if (FlowElement.Type.PARALLEL_GATEWAY.equals(flowElement.getType())
                    || FlowElement.Type.EXCLUSIVE_GATEWAY.equals(flowElement.getType())) {
                setNextFlowNodeIds(flowElementIds, (FlowNode) flowElement, runtimeContext);
                continue;
            }
            flowElementIds.add(flowElement.getId());
        }
    }

    private UserTask getPreviousUserTask(Task task, RuntimeContext runtimeContext, UserTask userTask) {
        boolean checkReject = Task.Status.PROCESSING.equals(task.getStatus())
                && ProcessStatus.RUNNING.equals(runtimeContext.getProcessInstance().getStatus());
        if (!checkReject) {
            throw new UserTaskRejectException();
        }
        Set<String> previousFlowNodeIds = new HashSet<>();
        setPreviousFlowNodeIds(previousFlowNodeIds, userTask, runtimeContext);
        ProcessInstance processInstance = runtimeContext.getProcessInstance();
        List<Task> allTasks = taskService.listTasksByInstanceId(processInstance.getInstanceId());
        allTasks = allTasks.stream().filter(t -> t.getEndDateTime() != null)
                .sorted(Comparator.comparing(Task::getEndDateTime).reversed()).collect(Collectors.toList());
        List<FlowElement> satisfyFlowElements = new ArrayList<>();
        for (Task finishTask : allTasks) {
            if (Task.Status.FINISH.equals(finishTask.getStatus()) && previousFlowNodeIds.contains(finishTask.getNodeId())) {
                FlowElement flowElement = runtimeContext.findFlowNode(finishTask.getNodeId());
                if (!flowElement.getType().equals(FlowElement.Type.USER_TASK)) {
                    throw new UserTaskRejectException();
                }
                satisfyFlowElements.add(flowElement);
            }
        }
        if (satisfyFlowElements.isEmpty()) {
            throw new UserTaskRejectException();
        }
        if (satisfyFlowElements.size() == 1) {
            return (UserTask) satisfyFlowElements.get(0);
        }
        return getPreviousUserTaskNotInCircle(runtimeContext, userTask, satisfyFlowElements);
    }

    private UserTask getPreviousUserTaskNotInCircle(RuntimeContext runtimeContext, UserTask userTask,
                                                    List<FlowElement> satisfyFlowElements) {
        Set<String> nextFlowNodeIds = new HashSet<>();
        setAllNextFlowNodeIds(nextFlowNodeIds, userTask, runtimeContext);
        FlowElement flowElement =
                satisfyFlowElements.stream()
                        .filter(element -> !nextFlowNodeIds.contains(element.getId())).findFirst().orElse(null);
        if (flowElement != null) {
            return (UserTask) flowElement;
        } else {
            return (UserTask) satisfyFlowElements.get(0);
        }
    }

    private void setAllNextFlowNodeIds(Set<String> flowElementIds, FlowNode flowNode,
                                       RuntimeContext runtimeContext) {
        // 获取任务节点的线
        List<SequenceFlow> nextSequenceFlows = runtimeContext.findNextSequenceFlows(flowNode.getOutgoing());
        for (SequenceFlow sequenceFlow : nextSequenceFlows) {
            FlowElement flowElement = runtimeContext.findNextFlowNode(sequenceFlow);
            if (flowElementIds.contains(flowElement.getId())) {
                continue;
            }
            flowElementIds.add(flowElement.getId());
            setAllNextFlowNodeIds(flowElementIds, (FlowNode) flowElement, runtimeContext);
        }
    }

    private void setPreviousFlowNodeIds(Set<String> flowElementIds, FlowNode flowNode, RuntimeContext runtimeContext) {
        // 获取任务节点的线
        List<SequenceFlow> previousSequenceFlows = runtimeContext.findNextSequenceFlows(flowNode.getIncoming());
        for (SequenceFlow sequenceFlow : previousSequenceFlows) {
            FlowElement flowElement = runtimeContext.findPreviousFlowNode(sequenceFlow);
            // 如果是排他网关则继续往上找
            if (FlowElement.Type.EXCLUSIVE_GATEWAY.equals(flowElement.getType())) {
                setPreviousFlowNodeIds(flowElementIds, (FlowNode) flowElement, runtimeContext);
                continue;
            }
            // 不允许并行网关
            if (FlowElement.Type.PARALLEL_GATEWAY.equals(flowElement.getType())) {
                throw new UserTaskRejectException();
            }
            flowElementIds.add(flowElement.getId());
        }
    }
}
