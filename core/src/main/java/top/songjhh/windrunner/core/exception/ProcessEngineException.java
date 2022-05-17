package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class ProcessEngineException extends RuntimeException {
    public ProcessEngineException() {
    }

    public ProcessEngineException(String message) {
        super(message);
    }

    public ProcessEngineException(String message, Throwable cause) {
        super(message, cause);
    }
}
