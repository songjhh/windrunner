package top.songjhh.windrunner.core.engine.executor;

import top.songjhh.windrunner.core.engine.identity.IdentityService;
import top.songjhh.windrunner.core.engine.process.ProcessService;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.task.TaskService;

import java.util.EnumMap;

/**
 * @author songjhh
 */
public class FlowExecutorFactory {

    private static final EnumMap<FlowElement.Type, FlowNodeExecutor> EXECUTOR_MAP =
            new EnumMap<>(FlowElement.Type.class);

    private FlowExecutorFactory() {
    }

    public static void create(ProcessService processService, TaskService taskService, IdentityService identityService) {
        EXECUTOR_MAP.put(FlowElement.Type.START_EVENT, new StartEventExecutor(processService, taskService, identityService));
        EXECUTOR_MAP.put(FlowElement.Type.END_EVENT, new EndEventExecutor(processService, taskService, identityService));
        EXECUTOR_MAP.put(FlowElement.Type.USER_TASK, new UserTaskExecutor(processService, taskService, identityService));
        EXECUTOR_MAP.put(FlowElement.Type.EXCLUSIVE_GATEWAY, new ExclusiveGatewayExecutor(processService, taskService, identityService));
        EXECUTOR_MAP.put(FlowElement.Type.PARALLEL_GATEWAY, new ParallelGatewayExecutor(processService, taskService, identityService));
    }

    public static FlowNodeExecutor getExecutor(FlowElement.Type type) {
        return EXECUTOR_MAP.get(type);
    }
}
