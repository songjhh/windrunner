package top.songjhh.windrunner.core.exception;

/**
 * @author songjhh
 */
public class DeploymentNotDeployException extends ProcessEngineException{
    public DeploymentNotDeployException() {
        super("can not start process by no deployed");
    }
}
