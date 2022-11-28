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
 * Created by @author songjhh
 */
class ProcessEngineTakeBackTest {

    /**
     * 开始 -> 任务A ->(退回) 任务B -> 结束
     */
    @Test
    void should_take_back_A() {
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

        // 退回
        Assertions.assertTrue(processEngine.getRuntimeService().canTakeBack(aTasks.get(0).getTaskId()));
        runtimeContext = processEngine.getRuntimeService().takeBack(aTasks.get(0).getTaskId());
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

    @Test
    void should_take_back_B() {
        final String source = TestFileUtils.getString("take-back-b.json");
        final String nextUserField = "nextUser";
        final String nextUserField2 = "nextUser2";
        final String firstTaskOwnerId = "A";
        final String secondTaskOwnerId = "B";
        final String thirdTaskOwnerId = "C";
        List<Task> aTasks;
        List<Task> bTasks;
        List<Task> cTasks;

        Map<String, Object> variables = new HashMap<>();
        variables.put(nextUserField, firstTaskOwnerId);

        // 创建引擎
        ProcessEngine processEngine = BpmnProcessEngineBuilder.builder().build();
        RuntimeContext runtimeContext = startProcess(processEngine, source, variables);

        // 认证任务A
        ProcessInstance processInstance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(ProcessStatus.RUNNING, processInstance.getStatus());
        aTasks = processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(1, aTasks.size());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(thirdTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
        // 提交任务
        Map<String, Object> firstTaskVariables = new HashMap<>();
        firstTaskVariables.put(nextUserField, secondTaskOwnerId);
        firstTaskVariables.put(nextUserField2, thirdTaskOwnerId);
        processEngine.getRuntimeService().commit(firstTaskOwnerId, aTasks.get(0).getTaskId(),
                firstTaskVariables);

        // 认证任务B & C
        processInstance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(ProcessStatus.RUNNING, processInstance.getStatus());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
        Assertions.assertEquals(1, processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
        Assertions.assertEquals(1, processEngine.getTaskService()
                .listCurrentTasksByUser(thirdTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());

        // 退回
        processEngine.getRuntimeService().takeBack(aTasks.get(0).getTaskId());
        aTasks = processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(1, aTasks.size());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId())
                .size());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(thirdTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId())
                .size());
        runtimeContext = processEngine.getRuntimeService().commit(firstTaskOwnerId, aTasks.get(0).getTaskId(),
                firstTaskVariables);

        // 认证任务B
        processInstance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(ProcessStatus.RUNNING, processInstance.getStatus());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId())
                .size());
        bTasks = processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        cTasks = processEngine.getTaskService()
                .listCurrentTasksByUser(thirdTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(1, bTasks.size());
        Assertions.assertEquals(1, cTasks.size());

        // 查看后再回退
        processEngine.getTaskService().getById(bTasks.get(0).getTaskId());
        final Task aCannotTakeBackTask = aTasks.get(0);
        Assertions.assertThrowsExactly(UserTaskTakeBackException.class,
                () -> processEngine.getRuntimeService().takeBack(aCannotTakeBackTask.getTaskId()));

        // 提交任务
        runtimeContext = processEngine.getRuntimeService().commit(secondTaskOwnerId, bTasks.get(0).getTaskId(), null);
        Assertions.assertEquals(ProcessStatus.COMPLETED, runtimeContext.getProcessInstance().getStatus());
    }

    @Test
    void should_take_back_C() {
        final String source = TestFileUtils.getString("take-back-c.json");
        final String nextUserField = "nextUser";
        final String nextUserField2 = "nextUser2";
        final String nextUserField3 = "nextUser3";
        final String nextUserField4 = "nextUser4";
        final String firstTaskOwnerId = "A";
        final String secondTaskOwnerId = "B";
        final String thirdTaskOwnerId = "C";
        final String fourTaskOwnerId = "D";
        List<Task> aTasks;
        List<Task> bTasks;
        List<Task> cTasks;
        List<Task> dTasks;

        Map<String, Object> variables = new HashMap<>();
        variables.put(nextUserField, firstTaskOwnerId);

        // 创建引擎
        ProcessEngine processEngine = BpmnProcessEngineBuilder.builder().build();
        RuntimeContext runtimeContext = startProcess(processEngine, source, variables);

        // 认证任务A
        ProcessInstance processInstance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(ProcessStatus.RUNNING, processInstance.getStatus());
        aTasks = processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(1, aTasks.size());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(thirdTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
        // 提交任务
        Map<String, Object> firstTaskVariables = new HashMap<>();
        firstTaskVariables.put(nextUserField2, secondTaskOwnerId);
        firstTaskVariables.put(nextUserField3, thirdTaskOwnerId);
        firstTaskVariables.put(nextUserField4, fourTaskOwnerId);
        processEngine.getRuntimeService().commit(firstTaskOwnerId, aTasks.get(0).getTaskId(),
                firstTaskVariables);

        // 认证任务B & C & D
        processInstance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(ProcessStatus.RUNNING, processInstance.getStatus());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
        Assertions.assertEquals(1, processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(thirdTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
        Assertions.assertEquals(1, processEngine.getTaskService()
                .listCurrentTasksByUser(fourTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());

        // 退回
        processEngine.getRuntimeService().takeBack(aTasks.get(0).getTaskId());
        aTasks = processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(1, aTasks.size());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId())
                .size());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(thirdTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId())
                .size());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(fourTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId())
                .size());
        runtimeContext = processEngine.getRuntimeService().commit(firstTaskOwnerId, aTasks.get(0).getTaskId(),
                firstTaskVariables);

        // 认证任务B
        processInstance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(ProcessStatus.RUNNING, processInstance.getStatus());
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId())
                .size());
        bTasks = processEngine.getTaskService()
                .listCurrentTasksByUser(secondTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        cTasks = processEngine.getTaskService()
                .listCurrentTasksByUser(thirdTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        dTasks = processEngine.getTaskService()
                .listCurrentTasksByUser(fourTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(1, bTasks.size());
        Assertions.assertEquals(0, cTasks.size());
        Assertions.assertEquals(1, dTasks.size());

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
                .setName("测试2")
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
