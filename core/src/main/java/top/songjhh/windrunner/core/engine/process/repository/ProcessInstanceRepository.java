package top.songjhh.windrunner.core.engine.process.repository;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;

import java.util.List;

/**
 * @author songjhh
 */
public interface ProcessInstanceRepository {

    /**
     * 保存
     *
     * @param processInstance 实例
     */
    void save(ProcessInstance processInstance);

    /**
     * 获取运行实例
     *
     * @param instanceId 实例id
     * @return 实例
     */
    ProcessInstance getInstanceById(String instanceId);

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

}
