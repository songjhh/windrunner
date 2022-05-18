package top.songjhh.windrunner.core.engine.listener;

import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.runtime.model.UserTask;
import top.songjhh.windrunner.core.engine.task.model.Task;

import java.util.Map;

/**
 * @author songjhh
 */
public class DelegateTask {

    private final ProcessInstance instance;
    private final UserTask userTask;
    private final Task task;

    public DelegateTask(ProcessInstance instance, UserTask userTask, Task task) {
        this.instance = instance;
        this.userTask = userTask;
        this.task = task;
    }

    public void addCandidateUsers(String candidateUser, String candidateUserName) {
        userTask.getCandidateUsers().add(candidateUser);
        userTask.getCandidateUserNames().add(candidateUserName);
    }

    public void addParticipants(String participant, String participantName) {
        userTask.getParticipants().add(participant);
        userTask.getParticipantNames().add(participantName);
    }

    public void setAssignee(String assignee, String assigneeName) {
        userTask.setAssignee(assignee);
        userTask.setAssigneeName(assigneeName);
    }

    public Map<String, Object> getVariables() {
        return instance.getVariables();
    }

    public String getStarter() {
        return instance.getStarter();
    }

    public String getStarterName() {
        return instance.getStarterName();
    }

    public UserTask getUserTask() {
        return userTask;
    }

    public Task getTask() {
        return task;
    }
}
