package top.songjhh.windrunner.core.engine.executor;

import lombok.extern.slf4j.Slf4j;
import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.listener.DelegateTask;
import top.songjhh.windrunner.core.engine.listener.TaskListener;
import top.songjhh.windrunner.core.engine.listener.TaskListenerEventType;
import top.songjhh.windrunner.core.engine.listener.TaskListenerFactory;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.FlowActivity;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.SequenceFlow;
import top.songjhh.windrunner.core.engine.runtime.model.UserTask;
import top.songjhh.windrunner.core.engine.task.TaskService;
import top.songjhh.windrunner.core.engine.task.model.Task;
import top.songjhh.windrunner.core.util.ConditionExpressionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author songjhh
 */
@Slf4j
public class UserTaskExecutor extends AbstractFlowNodeExecutor {

    public UserTaskExecutor(ProcessService processService, TaskService taskService, IdentityService identityService) {
        super(processService, taskService, identityService);
    }

    @Override
    public boolean preExecute(RuntimeContext context, FlowElement executeElement, SequenceFlow incomingSequenceFlow) {
        UserTask userTask = (UserTask) executeElement;
        ProcessInstance processInstance = context.getProcessInstance();
        if (!ConditionExpressionUtils.validCondition(incomingSequenceFlow.getConditionExpression(),
                processInstance.getVariables())) {
            return false;
        }

        notifyTaskEvent(userTask.getTaskListenerEvents(),
                new DelegateTask(processInstance, userTask, null), TaskListenerEventType.CREATE);

        List<Task> tasks = this.createTasks(processInstance, userTask);
        tasks.forEach(task -> notifyTaskEvent(userTask.getTaskListenerEvents(),
                new DelegateTask(processInstance, userTask, task), TaskListenerEventType.AFTER_CREATE));

        processInstance.getCurrentNodeIds().add(userTask.getId());
        processInstance.runNode(userTask.getId());
        processService.save(processInstance);
        return true;
    }

    @Override
    public void doExecute(RuntimeContext context, FlowElement executeElement, Task task) {
        UserTask userTask = (UserTask) executeElement;
        ProcessInstance processInstance = context.getProcessInstance();

        notifyTaskEvent(userTask.getTaskListenerEvents(),
                new DelegateTask(processInstance, userTask, task), TaskListenerEventType.COMPLETE);

        taskService.save(task);

        boolean canExecuteNext = this.updateProcessInstanceAndCheckLoop(userTask, processInstance, task);
        if (canExecuteNext) {
            executeNext(context, userTask.getOutgoing());
        }
    }

    private void notifyTaskEvent(List<UserTask.TaskListenerEvent> taskListenerEvents,
                                 DelegateTask delegateTask,
                                 TaskListenerEventType eventType) {
        List<String> eventNames = taskListenerEvents.stream()
                .filter(it -> eventType.equals(it.getEventType()))
                .map(UserTask.TaskListenerEvent::getEventName).collect(Collectors.toList());
        for (String eventName : eventNames) {
            TaskListener listener = TaskListenerFactory.getTaskListenerByEventName(eventName);
            if (listener == null) {
                log.warn("can not find task listener by event name: " + eventName);
                continue;
            }
            try {
                listener.notify(delegateTask);
            } catch (Exception e) {
                log.warn("can not find task listener by event name: " + eventName);
            }
        }
    }

    private List<Task> createTasks(ProcessInstance processInstance, UserTask userTask) {
        List<Task> result = new ArrayList<>();
        FlowActivity.MultiInstanceLoopCharacteristics characteristics =
                userTask.getMultiInstanceLoopCharacteristics();
        if (FlowActivity.MultiInstanceLoopCharacteristics.PARALLEL.equals(characteristics)) {
            for (int i = 0; i < userTask.getCandidateUsers().size(); i++) {
                userTask.setAssignee(userTask.getCandidateUsers().get(i));
                userTask.setAssigneeName(userTask.getCandidateUserNames().get(i));
                Task task = Task.create(processInstance, userTask);
                taskService.save(task);
                result.add(task);
            }
        } else {
            Task task = Task.create(processInstance, userTask);
            taskService.save(task);
            result.add(task);
        }
        return result;
    }

    private boolean updateProcessInstanceAndCheckLoop(UserTask userTask, ProcessInstance processInstance, Task task) {
        FlowActivity.MultiInstanceLoopCharacteristics characteristics =
                userTask.getMultiInstanceLoopCharacteristics();
        if (FlowActivity.MultiInstanceLoopCharacteristics.PARALLEL.equals(characteristics)) {
            boolean finishCondition = this.checkFinishCondition(processInstance.getInstanceId(), userTask.getId());
            if (finishCondition) {
                processInstance.completeNode(userTask.getId(), task.getVariables());
            } else {
                processInstance.putAllVariables(task.getVariables());
            }
            processService.save(processInstance);
            return finishCondition;
        } else {
            processInstance.completeNode(userTask.getId(), task.getVariables());
            processService.save(processInstance);
            return true;
        }
    }

    private boolean checkFinishCondition(String instanceId, String nodeId) {
        List<Task> tasks = taskService.listTasksByInstanceIdAndNodeId(instanceId, nodeId);
        for (Task task : tasks) {
            if (Task.Status.PROCESSING.equals(task.getStatus())) {
                return false;
            }
        }
        return true;
    }
}
