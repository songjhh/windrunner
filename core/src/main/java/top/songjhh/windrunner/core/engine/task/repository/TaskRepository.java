package top.songjhh.windrunner.core.engine.task.repository;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.task.model.Task;

import java.util.List;

/**
 * @author songjhh
 */
public interface TaskRepository {

    /**
     * 保存
     *
     * @param task 任务
     */
    void save(Task task);

    /**
     * 获取任务
     *
     * @param taskId 任务id
     * @return 任务
     */
    Task getById(String taskId);

    /**
     * 获取运行实例任务列表
     *
     * @param instanceId 流程实例id
     * @return 任务列表
     */
    List<Task> listTasksByInstanceId(String instanceId);

    /**
     * 获取运行实例以及节点的任务列表
     *
     * @param instanceId 流程实例id
     * @param nodeId     节点id
     * @return 任务列表
     */
    List<Task> listTasksByInstanceIdAndNodeId(String instanceId, String nodeId);

    /**
     * 查询任务列表
     *
     * @param query 查询条件
     * @return 任务列表
     */
    <T extends AdvancedPagedQuery> List<Task> list(T query);

    /**
     * 获取总数
     *
     * @param query 查询条件
     * @return 总数
     */
    <T extends AdvancedPagedQuery> long count(T query);

    /**
     * 获取人员的任务
     *
     * @param user       人员
     * @param instanceId 实例id
     * @return 任务列表
     */
    List<Task> listCurrentTasksByUser(String user, String instanceId);


    /**
     * 删除指定流程的所有任务
     *
     * @param instanceId 流程Id
     */
    void deleteByInstanceId(String instanceId);
}
