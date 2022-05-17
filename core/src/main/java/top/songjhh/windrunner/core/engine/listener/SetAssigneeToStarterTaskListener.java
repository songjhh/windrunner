package top.songjhh.windrunner.core.engine.listener;

/**
 * @author songjhh
 */
public class SetAssigneeToStarterTaskListener implements TaskListener {

    @Override
    public void notify(DelegateTask delegateTask) {
        delegateTask.setAssignee(delegateTask.getStarter(), delegateTask.getStarterName());
    }

}
