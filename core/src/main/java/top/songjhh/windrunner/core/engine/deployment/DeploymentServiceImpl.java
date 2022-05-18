package top.songjhh.windrunner.core.engine.deployment;

import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.deployment.model.DeploymentBuilder;
import top.songjhh.windrunner.core.engine.deployment.repository.DeploymentRepository;
import top.songjhh.windrunner.core.exception.NotFoundDeploymentException;

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
        // TODO: 校验流程
        deploymentRepository.save(deploymentBuilder.getDeployment());
    }

    @Override
    public Deployment getDeploymentById(String deploymentId) {
        Deployment deployment = deploymentRepository.getDeploymentById(deploymentId);
        if (deployment == null) {
            throw new NotFoundDeploymentException(deploymentId);
        }
        return deployment;
    }
}
