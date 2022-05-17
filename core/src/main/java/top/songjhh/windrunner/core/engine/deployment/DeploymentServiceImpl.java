package top.songjhh.windrunner.core.engine.deployment;

import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.deployment.model.DeploymentBuilder;
import top.songjhh.windrunner.core.engine.deployment.repository.DeploymentRepository;

/**
 * Created by @author songjhh
 */
public class DeploymentServiceImpl implements DeploymentService {

    private final DeploymentRepository deploymentRepository;

    public DeploymentServiceImpl(DeploymentRepository repository) {
        this.deploymentRepository = repository;
    }

    @Override
    public void save(DeploymentBuilder deploymentBuilder) {
        deploymentRepository.save(deploymentBuilder.getDeployment());
    }

    @Override
    public Deployment getDeploymentById(String deploymentId) {
        return deploymentRepository.getDeploymentById(deploymentId);
    }
}
