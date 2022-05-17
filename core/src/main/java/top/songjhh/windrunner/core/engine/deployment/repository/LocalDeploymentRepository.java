package top.songjhh.windrunner.core.engine.deployment.repository;

import top.songjhh.windrunner.core.engine.deployment.model.Deployment;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author songjhh
 */
public class LocalDeploymentRepository implements DeploymentRepository {

    private final Map<String, Deployment> deploymentMap;

    public LocalDeploymentRepository() {
        this.deploymentMap = new LinkedHashMap<>();
    }

    @Override
    public void save(Deployment deployment) {
        deploymentMap.put(deployment.getDeploymentId(), deployment);
    }

    @Override
    public Deployment getDeploymentById(String deploymentId) {
        return deploymentMap.get(deploymentId);
    }
}
