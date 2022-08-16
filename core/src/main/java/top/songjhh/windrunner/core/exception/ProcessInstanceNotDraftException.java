package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class ProcessInstanceNotDraftException extends ProcessEngineException{
    public ProcessInstanceNotDraftException() {
        super("can not find this instance or instance is not draft");
    }
}
