package top.songjhh.windrunner.core.engine.task.model;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.songjhh.windrunner.core.engine.runtime.model.UserEntity;
import top.songjhh.windrunner.core.engine.runtime.model.UserTask;
import top.songjhh.windrunner.core.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Created by @author songjhh
 */
@Getter
@Setter
@NoArgsConstructor
public class Task {

    /**
     * 任务id
     */
    private String taskId;
    /**
     * 流程运行id
     */
    private String instanceId;
    /**
     * 节点id
     */
    private String nodeId;
    /**
     * 任务名称
     */
    private String name;
    /**
     * 拥有者
     */
    private String owner;
    /**
     * 拥有者名称
     */
    private String ownerName;
    /**
     * 执行者
     */
    private String assignee;
    /**
     * 执行者名称
     */
    private String assigneeName;
    /**
     * 候选者
     */
    private List<String> candidateUsers;
    /**
     * 候选者名称
     */
    private List<String> candidateUserNames;
    /**
     * 参与者
     */
    private List<String> participants;
    /**
     * 参与者名称
     */
    private List<String> participantNames;
    /**
     * 状态
     */
    private Task.Status status;
    /**
     * 表单信息
     */
    private Map<String, Object> variables;
    /**
     * 开始时间
     */
    private LocalDateTime beginDateTime;
    /**
     * 结束时间
     */
    private LocalDateTime endDateTime;
    /**
     * 表单
     */
    private String formKey;

    private Task(String instanceId, UserTask userTask, Map<String, Object> variables) {
        this.taskId = NanoIdUtils.randomNanoId();
        this.instanceId = instanceId;
        this.nodeId = userTask.getId();
        this.owner = userTask.getAssignee();
        this.ownerName = userTask.getAssigneeName();
        this.assignee = userTask.getAssignee();
        this.assigneeName = userTask.getAssigneeName();
        this.candidateUsers = userTask.getCandidateUsers();
        this.candidateUserNames = userTask.getCandidateUserNames();
        this.participants = userTask.getParticipants();
        this.participantNames = userTask.getParticipantNames();
        this.name = userTask.getName();
        this.status = Status.PROCESSING;
        this.variables = Optional.ofNullable(variables).orElse(new HashMap<>());
        this.beginDateTime = LocalDateTime.now();
        this.formKey = userTask.getFormKey();
    }

    public static Task create(String instanceId, UserTask userTask, Map<String, Object> variables) {
        return new Task(instanceId, userTask, variables);
    }

    public Task complete(UserEntity assignee, Map<String, Object> variables) {
        finishTask(assignee, variables);
        this.status = Status.FINISH;
        return this;
    }

    private void finishTask(UserEntity assignee, Map<String, Object> variables) {
        putAllVariables(variables);
        if (assignee != null) {
            this.assignee = assignee.getId();
            this.assigneeName = assignee.getName();
            if (StringUtils.isEmpty(this.owner)) {
                this.owner = assignee.getId();
                this.owner = assignee.getName();
            }
        }
        this.endDateTime = LocalDateTime.now();
    }

    public void putAllVariables(Map<String, Object> variables) {
        if (variables != null) {
            this.variables.putAll(variables);
        }
    }

    public Task terminated() {
        this.status = Status.TERMINATED;
        return this;
    }

    public enum Status {
        /**
         * 进行中
         */
        PROCESSING,
        /**
         * 已完成
         */
        FINISH,
        /**
         * 驳回
         */
        REJECT,
        /**
         * 中止
         */
        TERMINATED
    }
}
