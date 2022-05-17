package top.songjhh.windrunner.core.engine.deployment.model;

import com.aventrix.jnanoid.jnanoid.NanoIdUtils;
import lombok.Getter;
import lombok.Setter;
import top.songjhh.windrunner.core.engine.runtime.converter.FlowModelConvertProvider;
import top.songjhh.windrunner.core.engine.runtime.model.DefinitionFileType;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.StartEvent;
import top.songjhh.windrunner.core.exception.FlowElementCastException;

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
        this.status = Status.DEPLOY;
    }

    public StartEvent getStartEvent() {
        for (FlowElement flowElement : FlowModelConvertProvider.converterToModel(source, type).getFlowElementList()) {
            if (FlowElement.Type.START_EVENT.equals(flowElement.getType())) {
                if (!(flowElement instanceof StartEvent)) {
                    throw new FlowElementCastException(StartEvent.class);
                }
                return (StartEvent) flowElement;
            }
        }
        return null;
    }

    public enum Status {
        /**
         * 已部署
         */
        DEPLOY,
        /**
         * 暂停
         */
        SUSPEND
    }
}
