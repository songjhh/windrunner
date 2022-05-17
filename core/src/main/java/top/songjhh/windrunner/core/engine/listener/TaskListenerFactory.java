package top.songjhh.windrunner.core.engine.listener;

import java.util.HashMap;
import java.util.Map;

/**
 * @author songjhh
 */
public class TaskListenerFactory {

    private static final Map<String, TaskListener> EVENT_MAP = new HashMap<>();

    private TaskListenerFactory() {
    }

    public static void register(TaskListener listener) {
        EVENT_MAP.put(listener.getClass().getSimpleName(), listener);
    }

    public static TaskListener getTaskListenerByEventName(String eventName) {
        return EVENT_MAP.get(eventName);
    }

}
