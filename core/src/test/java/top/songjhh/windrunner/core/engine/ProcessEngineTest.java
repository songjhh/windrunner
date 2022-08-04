package top.songjhh.windrunner.core.engine;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.process.model.ProcessStatus;
import top.songjhh.windrunner.core.engine.process.model.RuntimeContext;
import top.songjhh.windrunner.core.engine.runtime.model.DefinitionFileType;
import top.songjhh.windrunner.core.engine.task.model.Task;
import top.songjhh.windrunner.core.util.TestFileUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by @author songjhh
 */
class ProcessEngineTest {

    /**
     * 开始 -> 任务A -> 结束
     */
    @Test
    void should_begin_taskA_end() {
        final String source = TestFileUtils.getString("simple-one.json");
        final String firstTaskOwnerId = "1";

        Map<String, Object> variables = new HashMap<>();
        variables.put("name", "Sam");
        variables.put("age", 20);

        // 创建引擎
        ProcessEngine processEngine = BpmnProcessEngineBuilder.builder().build();
        RuntimeContext runtimeContext = startProcess(processEngine, source, null, variables);

        // 验证 RuntimeContext
        Assertions.assertNotNull(runtimeContext);
        Assertions.assertNotNull(runtimeContext.getProcessInstance().getVariables());

        // 获取实例
        ProcessInstance processInstance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());

        Assertions.assertEquals("Sam", processInstance.getVariables().get("name"));
        Assertions.assertEquals(20, (Integer) processInstance.getVariables().get("age"));
        Assertions.assertEquals(ProcessStatus.RUNNING, processInstance.getStatus());

        Assertions.assertEquals(1, processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, null).size());
        List<Task> tasks = processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(1, tasks.size());

        // 获取任务
        Task firstTask = tasks.get(0);
        Map<String, Object> firstTaskVariables = new HashMap<>();
        firstTaskVariables.put("age", 40);
        // 提交任务
        RuntimeContext context = processEngine.getRuntimeService().commit(firstTask.getTaskId(), firstTaskVariables);
        Assertions.assertEquals(40, context.getProcessInstance().getVariables().get("age"));

        // 获取新instance
        ProcessInstance nextProcessInstance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());

        Assertions.assertEquals(ProcessStatus.COMPLETED, nextProcessInstance.getStatus());
        Assertions.assertNotNull(nextProcessInstance.getEndDateTime());
        Assertions.assertNotNull(nextProcessInstance.getVariables());
        Assertions.assertEquals("Sam", nextProcessInstance.getVariables().get("name"));
        Assertions.assertEquals(40, (Integer) nextProcessInstance.getVariables().get("age"));
        Assertions.assertEquals(0, processEngine.getTaskService()
                .listCurrentTasksByUser(firstTaskOwnerId, runtimeContext.getProcessInstance().getInstanceId()).size());
    }

    @Test
    void should_begin_taskA_or_taskB_end() {
        final String source = TestFileUtils.getString("simple-taskA-or-taskB.json");
        final String instanceStarter = "starter";
        final String firstTaskOwnerId = "O";
        final String secondTaskOwnerIdForA = "A";
        final String secondTaskOwnerIdForB = "starter";
        ProcessEngine processEngine = BpmnProcessEngineBuilder.builder().build();
        RuntimeContext runtimeContext = startProcess(processEngine, source, instanceStarter, null);
        Assertions.assertEquals(instanceStarter, runtimeContext.getProcessInstance().getStarter());

        // 20
        Task firstTask = processEngine.getTaskService().listCurrentTasksByUser(firstTaskOwnerId,
                runtimeContext.getProcessInstance().getInstanceId()).get(0);
        processEngine.getRuntimeService().commit(firstTask.getTaskId(), Collections.singletonMap("age", 20));

        List<Task> aTasks = processEngine.getTaskService().listCurrentTasksByUser(secondTaskOwnerIdForA,
                runtimeContext.getProcessInstance().getInstanceId());
        List<Task> bTasks = processEngine.getTaskService().listCurrentTasksByUser(secondTaskOwnerIdForB,
                runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(1, aTasks.size());
        Assertions.assertEquals(0, bTasks.size());

        Task aTask = aTasks.get(0);
        processEngine.getRuntimeService().commit(aTask.getTaskId(), null);

        ProcessInstance instance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(ProcessStatus.COMPLETED, instance.getStatus());
        Assertions.assertEquals(20, instance.getVariables().get("age"));

        // 30
        runtimeContext = startProcess(processEngine, source, instanceStarter, null);

        firstTask = processEngine.getTaskService().listCurrentTasksByUser(firstTaskOwnerId,
                runtimeContext.getProcessInstance().getInstanceId()).get(0);
        processEngine.getRuntimeService().commit(firstTask.getTaskId(), Collections.singletonMap("age", 30));

        aTasks = processEngine.getTaskService().listCurrentTasksByUser(secondTaskOwnerIdForA,
                runtimeContext.getProcessInstance().getInstanceId());
        bTasks = processEngine.getTaskService().listCurrentTasksByUser(secondTaskOwnerIdForB,
                runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(0, aTasks.size());
        Assertions.assertEquals(1, bTasks.size());

        Task bTask = bTasks.get(0);
        processEngine.getRuntimeService().commit(bTask.getTaskId(), null);

        instance =
                processEngine.getProcessService().getInstanceById(runtimeContext.getProcessInstance().getInstanceId());
        Assertions.assertEquals(ProcessStatus.COMPLETED, instance.getStatus());
        Assertions.assertEquals(30, instance.getVariables().get("age"));
    }

    @Test
    void should_begin_multi_instance() {
        final String source = TestFileUtils.getString("multi-instance.json");
        final String instanceStarter = "starter";
        final String samOwnerId = "Sam";
        final String jackOwnerId = "Jack";

        ProcessEngine processEngine = BpmnProcessEngineBuilder.builder().build();
        RuntimeContext runtimeContext = startProcess(processEngine, source, instanceStarter, null);
        Assertions.assertEquals(instanceStarter, runtimeContext.getProcessInstance().getStarter());

        String instanceId = runtimeContext.getProcessInstance().getInstanceId();
        Assertions.assertEquals(2,
                processEngine.getProcessService().getInstanceById(instanceId).getCurrentNodeIds().size());
        Assertions.assertEquals(3,
                processEngine.getTaskService().listTasksByInstanceId(instanceId).size());

        Task bTaskFromStarter = processEngine.getTaskService().listCurrentTasksByUser(instanceStarter,
                instanceId).get(0);
        processEngine.getRuntimeService().commit(bTaskFromStarter.getTaskId(), Collections.singletonMap("age", 30));
        Assertions.assertEquals(1,
                processEngine.getProcessService().getInstanceById(instanceId).getCurrentNodeIds().size());
        Assertions.assertEquals(2,
                processEngine.getTaskService().listTasksByInstanceId(instanceId).stream().filter(it -> Task.Status.PROCESSING.equals(it.getStatus())).count());

        Task aTaskFromSam = processEngine.getTaskService().listCurrentTasksByUser(samOwnerId, instanceId).get(0);
        processEngine.getRuntimeService().commit(aTaskFromSam.getTaskId(), Collections.singletonMap("age", 20));
        Assertions.assertEquals(1,
                processEngine.getProcessService().getInstanceById(instanceId).getCurrentNodeIds().size());
        Assertions.assertEquals(1,
                processEngine.getTaskService().listTasksByInstanceId(instanceId).stream().filter(it -> Task.Status.PROCESSING.equals(it.getStatus())).count());

        Task aTaskFromJack = processEngine.getTaskService().listCurrentTasksByUser(jackOwnerId,
                instanceId).get(0);
        processEngine.getRuntimeService().commit(aTaskFromJack.getTaskId(), Collections.singletonMap("age", 20));
        Assertions.assertEquals(1,
                processEngine.getProcessService().getInstanceById(instanceId).getCurrentNodeIds().size());
        Assertions.assertEquals(1,
                processEngine.getTaskService().listTasksByInstanceId(instanceId).stream().filter(it -> Task.Status.PROCESSING.equals(it.getStatus())).count());

        Task cTaskFromStarter =
                processEngine.getTaskService().listCurrentTasksByUser(instanceStarter, instanceId).get(0);
        processEngine.getRuntimeService().commit(cTaskFromStarter.getTaskId(), Collections.singletonMap("age", 40));
        ProcessInstance instance = processEngine.getProcessService().getInstanceById(instanceId);
        Assertions.assertEquals(ProcessStatus.COMPLETED, instance.getStatus());
        Assertions.assertEquals(40, instance.getVariables().get("age"));
    }

    private RuntimeContext startProcess(ProcessEngine processEngine, String source, String starter, Map<String,
            Object> variables) {
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
                .startProcessByDeploymentId(starter, deploymentByRepos.getDeploymentId(), variables);
    }

}
