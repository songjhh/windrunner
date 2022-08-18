package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class UserTaskTakeBackException extends ProcessEngineException{
    public UserTaskTakeBackException() {
        super("can not take back");
    }
}
