package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class NotFoundProcessInstanceException extends ProcessEngineException{
    public NotFoundProcessInstanceException(String instanceId) {
        super("can not find process instance, instanceId: " + instanceId);
    }
}
