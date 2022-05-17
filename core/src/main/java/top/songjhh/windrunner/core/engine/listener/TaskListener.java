package top.songjhh.windrunner.core.engine.listener;

import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.UserTask;

/**
 * @author songjhh
 */
public interface TaskListener {

    void notify(DelegateTask delegateTask);

}
