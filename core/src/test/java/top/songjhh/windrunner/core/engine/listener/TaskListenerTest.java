package top.songjhh.windrunner.core.engine.listener;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import top.songjhh.windrunner.core.engine.BpmnProcessEngineBuilder;
import top.songjhh.windrunner.core.engine.runtime.model.UserTask;

/**
 * @author songjhh
 */
@Slf4j
class TaskListenerTest {

    // @Test
    // void should_register_and_execute() {
    //     BpmnProcessEngineBuilder.builder()
    //             .registerTaskListener(TaskListenerType.CREATE, new TaskListenerForTest())
    //             .build();
    //     TaskListenerFactory.getTaskListenerByTypeAndEventName(TaskListenerType.CREATE, "TaskListenerForTest").notify(null);
    // }
    //
    // static class TaskListenerForTest implements TaskListener {
    //
    //     @Override
    //     public void notify(UserTask userTask) {
    //         log.info("task listener notify");
    //     }
    // }

}
