package top.songjhh.windrunner.core.engine.deployment;

import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.deployment.model.DeploymentBuilder;

/**
 * Created by @author songjhh
 */
public interface DeploymentService {

    /**
     * 创建部署实例
     */
    default DeploymentBuilder createDeployment() {
        return DeploymentBuilder.build(this);
    }

    default DeploymentBuilder createDeployment(String deploymentId) {
        return DeploymentBuilder.build(this, deploymentId);
    }

    /**
     * 保存
     *
     * @param deploymentBuilder 实例Builder
     */
    void save(DeploymentBuilder deploymentBuilder);

    /**
     * 获取实例
     *
     * @param deploymentId 实例id
     * @return 实例
     */
    Deployment getDeploymentById(String deploymentId);

}
