package top.songjhh.windrunner.core.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.process.model.ProcessStatus;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.DefinitionFileType;
import top.songjhh.windrunner.core.engine.task.model.Task;
import top.songjhh.windrunner.core.exception.UserTaskTakeBackException;
import top.songjhh.windrunner.core.util.TestFileUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songjhh
 */
class ProcessEngineRejectTest {

    /**
     * 开始 -> 任务A ->(驳回) 任务B -> 结束
     */
    @Test
    void should_reject_A() {
        final String source = TestFileUtils.getString("take-back-a.json");
        final String nextUserField = "nextUser";
        final String firstTaskOwnerId = "A";
        final String secondTaskOwnerId = "B";
        List<Task> aTasks;
        List<Task> bTasks;

        Map<String, Object> variables = new HashMap<>();
        variables.put(nextUserField, firstTaskOwnerId);

        // 创建引擎
        ProcessEngine processEngine = BpmnProcessEngineBuilder.builder().build();
        RuntimeContext runtimeContext = startProcess(processEngine, source, variables);

        // 认证任务A
        ProcessInstance processInstance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(firstTaskOwnerId, processInstance.getVariables().get(nextUserField));
        Assertions.assertEquals(ProcessStatus.RUNNING, processInstance.getStatus());
        aTasks = processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(1, aTasks.size());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
        // 提交任务
        Map<String, Object> firstTaskVariables = new HashMap<>();
        firstTaskVariables.put(nextUserField, secondTaskOwnerId);
        runtimeContext = processEngine.getRuntimeService().commit(firstTaskOwnerId, aTasks.get(0).getTaskId(),
                firstTaskVariables);
        Assertions.assertEquals(secondTaskOwnerId,
                runtimeContext.getProcessInstance().getVariables().get(nextUserField));

        // 认证任务B
        processInstance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(secondTaskOwnerId, processInstance.getVariables().get(nextUserField));
        Assertions.assertEquals(ProcessStatus.RUNNING, processInstance.getStatus());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
        Assertions.assertEquals(1, processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
        Task secondUserFirstTask = processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).get(0);
        Assertions.assertEquals(1, aTasks.size());

        // 驳回
        runtimeContext = processEngine.getRuntimeService().reject(secondTaskOwnerId, secondUserFirstTask.getTaskId());
        Assertions.assertEquals(1, runtimeContext.getProcessInstance().getCurrentNodeIds().size());
        aTasks = processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(1, aTasks.size());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId())
                .size());

        runtimeContext = processEngine.getRuntimeService().commit(firstTaskOwnerId, aTasks.get(0).getTaskId(),
                firstTaskVariables);

        // 认证任务B
        processInstance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(secondTaskOwnerId, processInstance.getVariables().get(nextUserField));
        Assertions.assertEquals(ProcessStatus.RUNNING, processInstance.getStatus());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId())
                .size());
        bTasks = processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(1, bTasks.size());

        // 查看后再回退
        processEngine.getTaskService().getById(bTasks.get(0).getTaskId());
        final Task aCannotTakeBackTask = aTasks.get(0);
        Assertions.assertThrowsExactly(UserTaskTakeBackException.class,
                () -> processEngine.getRuntimeService().takeBack(aCannotTakeBackTask.getTaskId()));

        // 提交任务
        runtimeContext = processEngine.getRuntimeService().commit(secondTaskOwnerId, bTasks.get(0).getTaskId(), null);
        Assertions.assertEquals(ProcessStatus.COMPLETED, runtimeContext.getProcessInstance().getStatus());
    }

    private RuntimeContext startProcess(ProcessEngine processEngine, String source,
                                        Map<String, Object> variables) {
        // 部署
        Deployment deployment = processEngine.getDeploymentService().createDeployment()
                .setName("驳回测试")
                .setSource(source)
                .setType(DefinitionFileType.WIND_RUNNER_JSON)
                .deploy();
        // 获取部署
        Deployment deploymentByRepos =
                processEngine.getDeploymentService().getDeploymentById(deployment.getDeploymentId());
        // 开始流程
        return processEngine.getRuntimeService()
                .startProcessByDeploymentId("starter", deploymentByRepos.getDeploymentId(), variables);
    }
}
