package top.songjhh.windrunner.core.engine.process.executor;

import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;

/**
 * @author songjhh
 */
public interface ProcessNumberExecutor {

    /**
     * 生成流程 number
     *
     * @param processInstance 实例
     * @return 流程 number
     */
    String createProcessNumber(ProcessInstance processInstance);

}
