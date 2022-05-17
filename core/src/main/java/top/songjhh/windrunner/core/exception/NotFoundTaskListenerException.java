package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class NotFoundTaskListenerException extends ProcessEngineException {
    public NotFoundTaskListenerException(String message) {
        super("can not find task listener, event name: " + message);
    }
}
