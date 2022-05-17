package top.songjhh.windrunner.core.engine.runtime.model;

import lombok.Getter;
import lombok.Setter;
import top.songjhh.windrunner.core.engine.listener.TaskListenerEventType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by @author songjhh
 */
@Getter
public class UserTask extends FlowActivity {

    private String formKey;
    @Setter
    private String assignee;
    @Setter
    private String assigneeName;
    private final List<String> candidateUsers = new ArrayList<>();
    private final List<String> candidateUserNames = new ArrayList<>();
    private final List<String> participants = new ArrayList<>();
    private final List<String> participantNames = new ArrayList<>();
    private final List<TaskListenerEvent> taskListenerEvents = new ArrayList<>();

    @Override
    public Type getType() {
        return Type.USER_TASK;
    }

    public static class TaskListenerEvent {
        private final String eventName;
        private final TaskListenerEventType eventType;

        public TaskListenerEvent(String eventName, TaskListenerEventType eventType) {
            this.eventName = eventName;
            this.eventType = eventType;
        }

        public String getEventName() {
            return eventName;
        }

        public TaskListenerEventType getEventType() {
            return eventType;
        }
    }
}
