package top.songjhh.windrunner.core.engine.process.model;

import lombok.Getter;
import top.songjhh.windrunner.core.engine.runtime.model.UserEntity;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songjhh
 */
@Getter
public class ProcessInstanceBuilder {
    private String deploymentId;
    private String name;
    private String number;
    private Map<String, Object> variables;
    private String starter;
    private String starterName;
    private List<String> starterPlatforms;
    private List<String> starterDepartments;
    private LocalDateTime startDateTime;
    private ProcessStatus status;

    private ProcessInstanceBuilder() {
        this.variables = new HashMap<>();
    }

    public static ProcessInstanceBuilder builder() {
        return new ProcessInstanceBuilder();
    }

    public ProcessInstanceBuilder setDeploymentId(String deploymentId) {
        this.deploymentId = deploymentId;
        return this;
    }

    public ProcessInstanceBuilder setName(String name) {
        this.name = name;
        return this;
    }

    public ProcessInstanceBuilder setNumber(String number) {
        this.number = number;
        return this;
    }

    public ProcessInstanceBuilder setVariables(Map<String, Object> variables) {
        this.variables = variables;
        return this;
    }

    public ProcessInstanceBuilder setStarter(UserEntity starter) {
        this.starter = starter.getId();
        this.starterName = starter.getName();
        this.starterPlatforms = starter.getPlatforms();
        this.starterDepartments = starter.getDepartments();
        return this;
    }

    public ProcessInstance draft() {
        this.status = ProcessStatus.DRAFT;
        return new ProcessInstance(this);
    }

    public ProcessInstance start() {
        this.startDateTime = LocalDateTime.now();
        this.status = ProcessStatus.RUNNING;
        return new ProcessInstance(this);
    }
}