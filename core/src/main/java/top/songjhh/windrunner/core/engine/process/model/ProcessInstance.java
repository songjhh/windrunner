package top.songjhh.windrunner.core.engine.process.model;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 运行实例
 *
 * @author songjhh
 */
@Getter
@Setter
@NoArgsConstructor
public class ProcessInstance {

    /**
     * 实例id
     */
    private String instanceId;
    /**
     * 部署id
     */
    private String deploymentId;
    /**
     * 名称
     */
    private String name;
    /**
     * 运行开始时间
     */
    private LocalDateTime startDateTime;
    /**
     * 运行结束时间
     */
    private LocalDateTime endDateTime;
    /**
     * 当前运行到的节点
     */
    private Set<String> currentNodeIds;
    /**
     * 表单信息
     */
    private Map<String, Object> variables;
    /**
     * 运行实例状态
     */
    private ProcessStatus status;
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

    protected ProcessInstance(ProcessInstanceBuilder builder) {
        this.instanceId = NanoIdUtils.randomNanoId();
        this.deploymentId = builder.getDeploymentId();
        this.name = builder.getName();
        this.startDateTime = LocalDateTime.now();
        this.currentNodeIds = new HashSet<>();
        this.variables = Optional.ofNullable(builder.getVariables()).orElse(new HashMap<>());
        this.status = ProcessStatus.RUNNING;
        this.starter = builder.getStarter();
        this.starterName = builder.getStarterName();
        this.starterPlatforms = builder.getStarterPlatforms();
    }

    public void runNode(String nodeId) {
        currentNodeIds.add(nodeId);
    }

    public void completeNode(String nodeId, Map<String, Object> variables) {
        currentNodeIds.remove(nodeId);
        this.variables.putAll(variables);
    }

    public void putAllVariables(Map<String, Object> variables) {
        if (variables != null) {
            this.variables.putAll(variables);
        }
    }

    public void complete() {
        status = ProcessStatus.COMPLETED;
        endDateTime = LocalDateTime.now();
    }

}
