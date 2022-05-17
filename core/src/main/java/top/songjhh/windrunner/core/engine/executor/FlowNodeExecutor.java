package top.songjhh.windrunner.core.engine.executor;

import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.SequenceFlow;
import top.songjhh.windrunner.core.engine.task.model.Task;

/**
 * @author songjhh
 */
public interface FlowNodeExecutor {

    boolean preExecute(RuntimeContext context, FlowElement executeElement, SequenceFlow incomingSequenceFlow);
    void doExecute(RuntimeContext context, FlowElement executeElement, Task task);

}
