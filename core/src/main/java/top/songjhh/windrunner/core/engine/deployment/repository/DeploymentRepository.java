package top.songjhh.windrunner.core.engine.deployment.repository;

import top.songjhh.windrunner.core.engine.deployment.model.Deployment;

/**
 * @author songjhh
 */
public interface DeploymentRepository {

    /**
     * 保存
     *
     * @param deployment 部署实例
     */
    void save(Deployment deployment);

    /**
     * 获取实例
     *
     * @param deploymentId 实例id
     * @return 实例
     */
    Deployment getDeploymentById(String deploymentId);
}
