package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class FlowExpressionException extends ProcessEngineException {

    public FlowExpressionException(Throwable cause) {
        super("Can not parse the expression", cause);
    }
}
