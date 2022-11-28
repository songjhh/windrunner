package top.songjhh.windrunner.core.engine.runtime;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;

import java.util.List;
import java.util.Map;

/**
 * @author songjhh
 */
public interface RuntimeService {

    /**
     * 开始流程
     *
     * @param starter      发起者
     * @param deploymentId 部署实例id
     * @param variables    表单信息
     * @return 运行上下文
     */
    RuntimeContext startProcessByDeploymentId(String starter, String deploymentId, Map<String, Object> variables);

    /**
     * 草稿流程
     *
     * @param starter      发起者
     * @param deploymentId 部署实例id
     * @param variables    表单信息
     * @return 运行上下文
     */
    RuntimeContext draftProcessByDeploymentId(String starter, String deploymentId, Map<String, Object> variables);

    /**
     * 保存草稿流程
     *
     * @param instanceId 草稿实例id
     * @param variables  表单信息
     * @return 运行上下文
     */
    RuntimeContext saveDraft(String instanceId, Map<String, Object> variables);

    /**
     * 开始草稿流程
     *
     * @param instanceId 草稿实例id
     * @param variables  表单信息
     * @return 运行上下文
     */
    RuntimeContext startProcessByDraft(String instanceId, Map<String, Object> variables);

    /**
     * 提交
     *
     * @param assignee  执行人
     * @param taskId    任务id
     * @param variables 表单信息
     * @return 运行上下文
     */
    RuntimeContext commit(String assignee, String taskId, Map<String, Object> variables);

    /**
     * 提交
     *
     * @param taskId    任务id
     * @param variables 表单信息
     * @return 运行上下文
     */
    default RuntimeContext commit(String taskId, Map<String, Object> variables) {
        return commit(null, taskId, variables);
    }

    /**
     * 保存
     *
     * @param taskId    任务id
     * @param variables 表单信息
     * @return 运行上下文
     */
    RuntimeContext save(String taskId, Map<String, Object> variables);

    /**
     * 判断任务是否可拿回
     *
     * @param taskId 任务id
     * @return 结果
     */
    boolean canTakeBack(String taskId);

    /**
     * 拿回
     *
     * @param taskId 要拿回的任务
     * @return 运行上下文
     */
    RuntimeContext takeBack(String taskId);

    /**
     * 转办
     *
     * @param taskId 任务id
     * @param fromUserId 转办人id
     * @param toUserId 被转办人id
     * @return 运行上下文
     */
    RuntimeContext transfer(String taskId, String fromUserId, String toUserId);

    /**
     * 获取列表
     *
     * @param query 查询条件
     * @return 运行实例上下文列表
     */
    <T extends AdvancedPagedQuery> List<RuntimeContext> list(T query);

    /**
     * 获取上下文
     *
     * @param instanceId 实例id
     * @return 上下文
     */
    RuntimeContext getByInstanceId(String instanceId);
}
