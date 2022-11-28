package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class UserTaskTransferException extends ProcessEngineException{
    public UserTaskTransferException() {
        super("can not transfer task");
    }

    public UserTaskTransferException(String message) {
        super(message);
    }
}
