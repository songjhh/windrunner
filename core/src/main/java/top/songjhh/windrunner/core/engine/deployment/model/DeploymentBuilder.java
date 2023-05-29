package top.songjhh.windrunner.core.engine.deployment.model;

import top.songjhh.windrunner.core.engine.deployment.DeploymentService;
import top.songjhh.windrunner.core.engine.runtime.model.DefinitionFileType;

import java.util.Map;

/**
 * @author songjhh
 */
public class DeploymentBuilder {

    private final DeploymentService deploymentService;
    private final Deployment deployment;

    private DeploymentBuilder(DeploymentService deploymentService, String deploymentId) {
        this.deploymentService = deploymentService;
        this.deployment = Deployment.createDeployment(deploymentId);
    }

    public static DeploymentBuilder build(DeploymentService deploymentService) {
        return new DeploymentBuilder(deploymentService, null);
    }

    public static DeploymentBuilder build(DeploymentService deploymentService, String deploymentId) {
        return new DeploymentBuilder(deploymentService, deploymentId);
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

    public DeploymentBuilder setVariables(Map<String, Object> variables) {
        deployment.setVariables(variables);
        return this;
    }

    public Deployment getDeployment() {
        return deployment;
    }

    public Deployment deploy() {
        this.deployment.deployed();
        deploymentService.save(this);
        return deployment;
    }

    public Deployment draft() {
        this.deployment.draft();
        deploymentService.save(this);
        return deployment;
    }
}