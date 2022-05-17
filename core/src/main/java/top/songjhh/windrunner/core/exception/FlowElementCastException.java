package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class FlowElementCastException extends ProcessEngineException {
    public FlowElementCastException(Class<?> clazz) {
        super("Can not cast to " + clazz.getName());
    }
}
