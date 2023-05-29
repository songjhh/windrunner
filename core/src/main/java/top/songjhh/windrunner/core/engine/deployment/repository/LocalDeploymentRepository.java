package top.songjhh.windrunner.core.engine.deployment.repository;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.deployment.model.Deployment;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
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

    @Override
    public <T extends AdvancedPagedQuery> List<Deployment> list(T query) {
        return Collections.emptyList();
    }

    @Override
    public <T extends AdvancedPagedQuery> long count(T query) {
        return 0;
    }
}
