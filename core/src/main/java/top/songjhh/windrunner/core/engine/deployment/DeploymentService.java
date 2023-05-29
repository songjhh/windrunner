package top.songjhh.windrunner.core.engine.deployment;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.deployment.model.DeploymentBuilder;

import java.util.List;

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

    /**
     * 查询实例列表
     *
     * @param query 查询条件
     * @return 任务列表
     */
    <T extends AdvancedPagedQuery> List<Deployment> list(T query);

    /**
     * 获取总数
     *
     * @param query 查询条件
     * @return 总数
     */
    <T extends AdvancedPagedQuery> long count(T query);

}
