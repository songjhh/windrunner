package top.songjhh.windrunner.core.engine.runtime.model;

import lombok.Getter;

/**
 * Created by @author songjhh
 */
@Getter
public abstract class FlowElement {

    private String id;
    private String name;
    private String documentation;
    protected FlowElement.Type type;

    public abstract Type getType();

    public enum Type {
        /**
         * 开始事件
         */
        START_EVENT,
        /**
         * 结束事件
         */
        END_EVENT,
        /**
         * 顺序线
         */
        SEQUENCE_FLOW,
        /**
         * 用户任务
         */
        USER_TASK,
        /**
         * 排他网关
         */
        EXCLUSIVE_GATEWAY,
        /**
         * 并行网关
         */
        PARALLEL_GATEWAY

    }
}
