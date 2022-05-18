package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class NotFoundStartEventException extends ProcessEngineException{
    public NotFoundStartEventException() {
        super("can not find start event");
    }
}
