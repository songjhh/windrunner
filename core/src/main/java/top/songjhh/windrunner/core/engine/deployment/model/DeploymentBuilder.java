package top.songjhh.windrunner.core.engine.deployment.model;

import top.songjhh.windrunner.core.engine.deployment.DeploymentService;
import top.songjhh.windrunner.core.engine.runtime.model.DefinitionFileType;

/**
 * @author songjhh
 */
public class DeploymentBuilder {

    private final DeploymentService deploymentService;
    private final Deployment deployment;

    private DeploymentBuilder(DeploymentService deploymentService) {
        this.deploymentService = deploymentService;
        this.deployment = Deployment.createDeployment();
    }

    public static DeploymentBuilder build(DeploymentService deploymentService) {
        return new DeploymentBuilder(deploymentService);
    }

    public DeploymentBuilder setName(String name) {
        deployment.setName(name);
        return this;
    }

    public DeploymentBuilder setSource(String source) {
        deployment.setSource(source);
        return this;
    }

    public DeploymentBuilder setType(DefinitionFileType type) {
        deployment.setType(type);
        return this;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public Deployment deploy() {
        deploymentService.save(this);
        return deployment;
    }
}