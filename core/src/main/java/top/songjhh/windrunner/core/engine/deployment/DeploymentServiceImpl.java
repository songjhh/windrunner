package top.songjhh.windrunner.core.engine.deployment;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.deployment.model.DeploymentBuilder;
import top.songjhh.windrunner.core.engine.deployment.repository.DeploymentRepository;
import top.songjhh.windrunner.core.exception.NotFoundDeploymentException;

import java.util.List;

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
        Deployment deployment = deploymentRepository.getDeploymentById(deploymentId);
        if (deployment == null) {
            throw new NotFoundDeploymentException(deploymentId);
        }
        return deployment;
    }

    @Override
    public <T extends AdvancedPagedQuery> List<Deployment> list(T query) {
        return deploymentRepository.list(query);
    }

    @Override
    public <T extends AdvancedPagedQuery> long count(T query) {
        return deploymentRepository.count(query);
    }
}
