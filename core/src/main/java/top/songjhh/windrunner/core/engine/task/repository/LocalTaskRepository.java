package top.songjhh.windrunner.core.engine.task.repository;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import com.google.common.base.Strings;
import top.songjhh.windrunner.core.engine.task.model.Task;
import top.songjhh.windrunner.core.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author songjhh
 */
public class LocalTaskRepository implements TaskRepository {
    private final Map<String, Task> taskMap;

    public LocalTaskRepository() {
        this.taskMap = new LinkedHashMap<>();
    }

    @Override
    public void save(Task task) {
        taskMap.put(task.getTaskId(), task);
    }

    @Override
    public List<Task> listCurrentTasksByUser(String user, String instanceId) {
        List<Task> result = new ArrayList<>();
        for (Map.Entry<String, Task> entry : taskMap.entrySet()) {
            Task task = entry.getValue();
            if (!Task.Status.PROCESSING.equals(task.getStatus())) {
                continue;
            }
            boolean canAdd = StringUtils.isEmpty(user)
                    || user.equals(task.getAssignee())
                    || (StringUtils.isEmpty(task.getAssignee()) && task.getCandidateUsers().contains(user));
            canAdd &= StringUtils.isEmpty(instanceId) || instanceId.equals(task.getInstanceId());
            if (canAdd) {
                result.add(task);
            }
        }
        return result;
    }

    @Override
    public void deleteByInstanceId(String instanceId) {
        if (Strings.isNullOrEmpty(instanceId)) {
            return;
        }
        Iterator<Map.Entry<String, Task>> iterator = taskMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Task> next = iterator.next();
            Task task = next.getValue();
            if (instanceId.equals(task.getInstanceId())) {
                iterator.remove();
            }
        }
    }

    @Override
    public Task getById(String taskId) {
        return taskMap.get(taskId);
    }

    @Override
    public List<Task> listTasksByInstanceId(String instanceId) {
        return taskMap.values().stream().filter(it -> instanceId.equals(it.getInstanceId())).collect(Collectors.toList());
    }

    @Override
    public List<Task> listTasksByInstanceIdAndNodeId(String instanceId, String nodeId) {
        return taskMap.values().stream().filter(it -> instanceId.equals(it.getInstanceId()) && nodeId.equals(it.getNodeId()))
                .collect(Collectors.toList());
    }

    @Override
    public <T extends AdvancedPagedQuery> List<Task> list(T query) {
        return Collections.emptyList();
    }

    @Override
    public <T extends AdvancedPagedQuery> long count(T query) {
        return 0;
    }
}
