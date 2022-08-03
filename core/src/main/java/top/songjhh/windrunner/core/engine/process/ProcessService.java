package top.songjhh.windrunner.core.engine.process;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.runtime.model.UserEntity;

import java.util.List;
import java.util.Map;

/**
 * @author songjhh
 */
public interface ProcessService {

    /**
     * 开始流程
     *
     * @param starter    发起者
     * @param deployment 部署实例
     * @param variables  表单信息
     * @return 运行实例
     */
    ProcessInstance startProcessByDeployment(UserEntity starter, Deployment deployment, Map<String, Object> variables);

    /**
     * 获取运行实例
     *
     * @param instanceId 实例id
     * @return 实例
     */
    ProcessInstance getInstanceById(String instanceId);

    /**
     * 保存
     *
     * @param processInstance 实例
     */
    void save(ProcessInstance processInstance);

    /**
     * 获取列表
     *
     * @param query 查询条件
     * @return 运行实例列表
     */
    <T extends AdvancedPagedQuery> List<ProcessInstance> list(T query);

    /**
     * 获取总数
     *
     * @param query 查询条件
     * @return 总数
     */
    <T extends AdvancedPagedQuery> long count(T query);

    /**
     * 删除流程
     *
     * @param instanceId 实例id
     */
    void deleteById(String instanceId);

}
