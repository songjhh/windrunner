package top.songjhh.windrunner.core.engine.executor;

import ognl.Ognl;
import ognl.OgnlException;
import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.listener.DelegateTask;
import top.songjhh.windrunner.core.engine.listener.TaskListener;
import top.songjhh.windrunner.core.engine.listener.TaskListenerEventType;
import top.songjhh.windrunner.core.engine.listener.TaskListenerFactory;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.SequenceFlow;
import top.songjhh.windrunner.core.engine.runtime.model.UserTask;
import top.songjhh.windrunner.core.engine.task.TaskService;
import top.songjhh.windrunner.core.engine.task.model.Task;
import top.songjhh.windrunner.core.exception.FlowExpressionException;
import top.songjhh.windrunner.core.exception.NotFoundTaskListenerException;
import top.songjhh.windrunner.core.util.StringUtils;

import java.util.List;

/**
 * @author songjhh
 */
public class UserTaskExecutor extends AbstractFlowNodeExecutor {

    public UserTaskExecutor(ProcessService processService, TaskService taskService, IdentityService identityService) {
        super(processService, taskService, identityService);
    }

    @Override
    public boolean preExecute(RuntimeContext context, FlowElement executeElement, SequenceFlow incomingSequenceFlow) {
        UserTask userTask = (UserTask) executeElement;
        if (!validCondition(context, incomingSequenceFlow)) {
            return false;
        }

        userTask.getTaskListenerEvents().stream()
                .filter(it -> TaskListenerEventType.CREATE.equals(it.getEventType()))
                .map(UserTask.TaskListenerEvent::getEventName)
                .forEach(name -> {
                    TaskListener listener = TaskListenerFactory.getTaskListenerByEventName(name);
                    if (listener == null) {
                        throw new NotFoundTaskListenerException(name);
                    }
                    listener.notify(new DelegateTask(context.getProcessInstance(), userTask));
                });

        Task task = Task.create(context.getProcessInstance().getInstanceId(), userTask,
                context.getProcessInstance().getVariables());
        taskService.save(task);

        ProcessInstance processInstance = context.getProcessInstance();
        processInstance.getCurrentNodeIds().add(userTask.getId());
        processInstance.runNode(userTask.getId());
        processService.save(processInstance);
        return true;
    }

    @Override
    public void doExecute(RuntimeContext context, FlowElement executeElement, Task task) {
        UserTask userTask = (UserTask) executeElement;

        taskService.save(task);
        ProcessInstance processInstance = context.getProcessInstance();
        processInstance.completeNode(userTask.getId(), task.getVariables());
        processService.save(processInstance);

        List<SequenceFlow> nextSequenceFlowList = context.findNextSequenceFlows(userTask.getOutgoing());
        for (SequenceFlow sequenceFlow : nextSequenceFlowList) {
            FlowElement nextFlowNode = context.findNextFlowNode(sequenceFlow);
            FlowExecutorFactory.getExecutor(nextFlowNode.getType()).preExecute(context, nextFlowNode, sequenceFlow);
        }
    }

    private boolean validCondition(RuntimeContext context, SequenceFlow sequenceFlow) {
        if (StringUtils.isNotEmpty(sequenceFlow.getConditionExpression())) {
            try {
                Object expression = Ognl.parseExpression(sequenceFlow.getConditionExpression());
                Object expressionValue = Ognl.getValue(expression, context.getProcessInstance().getVariables());
                return Boolean.TRUE.equals(expressionValue);
            } catch (OgnlException e) {
                throw new FlowExpressionException(e);
            }
        }
        return true;
    }
}
