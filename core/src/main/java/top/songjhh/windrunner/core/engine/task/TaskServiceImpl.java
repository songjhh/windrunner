package top.songjhh.windrunner.core.engine.task;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.task.model.Task;
import top.songjhh.windrunner.core.engine.task.repository.TaskRepository;

import java.util.List;

/**
 * @author songjhh
 */
public class TaskServiceImpl implements TaskService {

    private final TaskRepository taskRepository;

    public TaskServiceImpl(TaskRepository repository) {
        this.taskRepository = repository;
    }

    @Override
    public void save(Task task) {
        taskRepository.save(task);
    }

    @Override
    public List<Task> listCurrentTasksByUser(String user, String instanceId) {
        return taskRepository.listCurrentTasksByUser(user, instanceId);
    }

    @Override
    public Task getById(String taskId) {
        return taskRepository.getById(taskId);
    }

    @Override
    public <T extends AdvancedPagedQuery> List<Task> list(T query) {
        return taskRepository.list(query);
    }

    @Override
    public <T extends AdvancedPagedQuery> long count(T query) {
        return taskRepository.count(query);
    }

    @Override
    public List<Task> listTasksByInstanceId(String instanceId) {
        return taskRepository.listTasksByInstanceId(instanceId);
    }
}
