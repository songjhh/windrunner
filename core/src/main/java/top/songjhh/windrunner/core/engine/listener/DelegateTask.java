package top.songjhh.windrunner.core.engine.listener;

import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.runtime.model.UserTask;

import java.util.Map;

/**
 * @author songjhh
 */
public class DelegateTask {

    private final ProcessInstance instance;
    private final UserTask userTask;

    public DelegateTask(ProcessInstance instance, UserTask userTask) {
        this.instance = instance;
        this.userTask = userTask;
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
}
