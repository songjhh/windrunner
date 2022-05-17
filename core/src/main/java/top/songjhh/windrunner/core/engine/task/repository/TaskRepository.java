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

    List<Task> listCurrentTasksByUser(String user, String instanceId);
}
