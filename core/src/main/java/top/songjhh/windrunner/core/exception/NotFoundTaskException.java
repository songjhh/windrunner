package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class NotFoundTaskException extends ProcessEngineException{
    public NotFoundTaskException(String taskId) {
        super("can not find task, taskId: " + taskId);
    }
}
