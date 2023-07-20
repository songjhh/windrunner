package top.songjhh.windrunner.core.engine.task.model;

import cc.ldsd.common.annotation.JacksonDateTimeFormat2Slash;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import com.google.gson.Gson;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.runtime.model.UserEntity;
import top.songjhh.windrunner.core.engine.runtime.model.UserTask;

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
     * 流程运行number
     */
    private String instanceNumber;
    /**
     * 部署id
     */
    private String deploymentId;
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
     * 驳回信息
     */
    private String rejectMessage;
    /**
     * 表单信息
     */
    private Map<String, Object> variables;
    /**
     * 开始时间
     */
    @JacksonDateTimeFormat2Slash
    private LocalDateTime beginDateTime;
    /**
     * 结束时间
     */
    @JacksonDateTimeFormat2Slash
    private LocalDateTime endDateTime;
    /**
     * 表单
     */
    private String formKey;
    /**
     * 是否是内置表单
     */
    private Boolean innerForm;
    /**
     * 发起者
     */
    private String starter;
    /**
     * 发起者名称
     */
    private String starterName;
    /**
     * 发起者所在单位
     */
    private List<String> starterPlatforms;
    /**
     * 发起者所在部门
     */
    private List<String> starterDepartments;
    /**
     * 流程开始时间
     */
    @JacksonDateTimeFormat2Slash
    private LocalDateTime startDateTime;
    /**
     * 是否已读
     */
    private Boolean read;

    private Task(ProcessInstance processInstance, UserTask userTask) {
        this.taskId = NanoIdUtils.randomNanoId();
        this.instanceId = processInstance.getInstanceId();
        this.instanceNumber = processInstance.getNumber();
        this.deploymentId = processInstance.getDeploymentId();
        this.nodeId = userTask.getId();
        this.assignee = userTask.getAssignee();
        this.assigneeName = userTask.getAssigneeName();
        this.candidateUsers = userTask.getCandidateUsers();
        this.candidateUserNames = userTask.getCandidateUserNames();
        this.participants = userTask.getParticipants();
        this.participantNames = userTask.getParticipantNames();
        this.name = userTask.getName();
        this.status = Status.PROCESSING;
        this.variables = Optional.ofNullable(processInstance.getVariables()).orElse(new HashMap<>());
        this.beginDateTime = LocalDateTime.now();
        this.formKey = userTask.getFormKey();
        this.innerForm = userTask.getInnerForm();
        this.starter = processInstance.getStarter();
        this.starterName = processInstance.getStarterName();
        this.starterPlatforms = processInstance.getStarterPlatforms();
        this.starterDepartments = processInstance.getStarterDepartments();
        this.startDateTime = processInstance.getStartDateTime();
        this.read = false;
    }

    public static Task create(ProcessInstance processInstance, UserTask userTask) {
        return new Task(processInstance, userTask);
    }

    public Task copy() {
        Task newTask = new Gson().fromJson(new Gson().toJson(this), Task.class);
        newTask.setTaskId(NanoIdUtils.randomNanoId());
        return newTask;
    }

    public Task complete(UserEntity assignee, Map<String, Object> variables) {
        finishTask(assignee, variables);
        this.status = Status.FINISH;
        return this;
    }

    public void reject(UserEntity assignee, String rejectMessage) {
        finishTask(assignee, null);
        this.rejectMessage = rejectMessage;
        this.status = Status.REJECT;
    }

    public void putAllVariables(Map<String, Object> variables) {
        if (variables != null) {
            this.variables.putAll(variables);
        }
    }

    public Task terminate() {
        this.status = Status.TERMINATED;
        this.endDateTime = LocalDateTime.now();
        return this;
    }

    public Task takeBack() {
        this.status = Status.TAKE_BACK;
        return this;
    }

    public void transfered(UserEntity assignee) {
        finishTask(assignee, null);
        this.status = Status.TRANSFER;
    }

    public void transfer(UserEntity from, UserEntity to) {
        if (assignee != null) {
            this.assignee = to.getId();
            this.assigneeName = to.getName();
        } else if (from != null && this.candidateUsers.contains(from.getId())) {
            int index = this.candidateUsers.indexOf(from.getId());
            this.candidateUsers.set(index, to.getId());
            this.candidateUserNames.set(index, to.getName());
        } else {
            this.candidateUsers.add(to.getId());
            this.candidateUserNames.add(to.getName());
        }
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
         * 拿回
         */
        TAKE_BACK,
        /**
         * 中止
         */
        TERMINATED,
        /**
         * 已转办
         */
        TRANSFER
    }

    private void finishTask(UserEntity assignee, Map<String, Object> variables) {
        putAllVariables(variables);
        if (assignee != null) {
            this.assignee = assignee.getId();
            this.assigneeName = assignee.getName();
        }
        this.endDateTime = LocalDateTime.now();
    }
}
