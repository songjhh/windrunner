package top.songjhh.windrunner.core.engine.deployment.model;

import cc.ldsd.common.annotation.JacksonDateTimeFormat2Slash;
import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.Getter;
import lombok.Setter;
import top.songjhh.windrunner.core.engine.runtime.model.DefinitionFileType;
import top.songjhh.windrunner.core.engine.runtime.model.StartEvent;
import top.songjhh.windrunner.core.util.FlowElementUtils;

import java.time.LocalDateTime;

/**
 * 部署实例
 * <p>
 * Created by @author songjhh
 */
@Getter
@Setter
public class Deployment {

    /**
     * 实例id
     */
    private String deploymentId;
    /**
     * 名称
     */
    private String name;
    /**
     * 部署时间
     */
    @JacksonDateTimeFormat2Slash
    private LocalDateTime deploymentDateTime;
    /**
     * 流程
     */
    private String source;
    /**
     * 解析类型
     */
    private DefinitionFileType type;
    /**
     * 状态
     */
    private Deployment.Status status;

    public static Deployment createDeployment() {
        Deployment deployment = new Deployment();
        deployment.setDeploymentId(NanoIdUtils.randomNanoId());
        deployment.setDeploymentDateTime(LocalDateTime.now());
        deployment.setStatus(Status.DEPLOYED);
        return deployment;
    }

    public StartEvent findStartEvent() {
        return FlowElementUtils.getStartEvent(source, type);
    }

    public void suspend() {
        this.status = Status.SUSPEND;
    }

    public enum Status {
        /**
         * 已部署
         */
        DEPLOYED,
        /**
         * 暂停
         */
        SUSPEND
    }
}
