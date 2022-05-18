package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class NotFoundDeploymentException extends ProcessEngineException{
    public NotFoundDeploymentException(String deploymentId) {
        super("can not find deployment, deploymentId: " + deploymentId);
    }
}
