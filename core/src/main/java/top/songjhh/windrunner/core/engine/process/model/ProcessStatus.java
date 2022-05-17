package top.songjhh.windrunner.core.engine.process.model;

/**
 * @author songjhh
 */
public enum ProcessStatus {
    /**
     * 运行中
     */
    RUNNING,
    /**
     * 结束
     */
    COMPLETED,
    /**
     * 暂缓
     */
    SUSPENDED,
    /**
     * 终止
     */
    TERMINATED;
}
