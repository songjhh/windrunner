package top.songjhh.windrunner.core.engine.deployment.model;

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
    private final String deploymentId;
    /**
     * 名称
     */
    private String name;
    /**
     * 部署时间
     */
    private final LocalDateTime deploymentDateTime;
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

    public Deployment() {
        this.deploymentId = NanoIdUtils.randomNanoId();
        this.deploymentDateTime = LocalDateTime.now();
        this.status = Status.DEPLOYED;
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
