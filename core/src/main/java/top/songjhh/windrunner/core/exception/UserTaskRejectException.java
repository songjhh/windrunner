package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class UserTaskRejectException extends ProcessEngineException{
    public UserTaskRejectException() {
        super("can not reject");
    }
}
