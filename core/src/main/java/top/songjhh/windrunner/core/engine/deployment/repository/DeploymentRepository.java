package top.songjhh.windrunner.core.engine.deployment.repository;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.deployment.model.Deployment;

import java.util.List;

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
