package top.songjhh.windrunner.core.engine.process;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstanceBuilder;
import top.songjhh.windrunner.core.engine.process.model.ProcessStatus;
import top.songjhh.windrunner.core.engine.process.repository.ProcessInstanceRepository;
import top.songjhh.windrunner.core.engine.runtime.model.UserEntity;
import top.songjhh.windrunner.core.exception.NotFoundProcessInstanceException;
import top.songjhh.windrunner.core.exception.ProcessInstanceNotDraftException;

import java.util.List;
import java.util.Map;

/**
 * @author songjhh
 */
public class ProcessServiceImpl implements ProcessService {

    private final ProcessInstanceRepository processInstanceRepository;

    public ProcessServiceImpl(ProcessInstanceRepository repository) {
        this.processInstanceRepository = repository;
    }

    @Override
    public ProcessInstance startProcessByDeployment(UserEntity starter, Deployment deployment,
                                                    Map<String, Object> variables) {
        ProcessInstance processInstance = ProcessInstanceBuilder.builder()
                .setDeploymentId(deployment.getDeploymentId())
                .setName(deployment.getName())
                .setVariables(variables)
                .setStarter(starter)
                .start();
        processInstanceRepository.save(processInstance);
        return processInstance;
    }

    @Override
    public ProcessInstance draftProcessByDeployment(UserEntity starter, Deployment deployment,
                                                    Map<String, Object> variables) {
        ProcessInstance processInstance = ProcessInstanceBuilder.builder()
                .setDeploymentId(deployment.getDeploymentId())
                .setName(deployment.getName())
                .setVariables(variables)
                .setStarter(starter)
                .draft();
        processInstanceRepository.save(processInstance);
        return processInstance;
    }

    @Override
    public ProcessInstance startProcessByDraft(String instanceId, Map<String, Object> variables) {
        ProcessInstance processInstance = processInstanceRepository.getInstanceById(instanceId);
        if (!ProcessStatus.DRAFT.equals(processInstance.getStatus())) {
            throw new ProcessInstanceNotDraftException();
        }
        processInstance.setVariables(variables);
        processInstance.start();
        processInstanceRepository.save(processInstance);
        return processInstance;
    }

    @Override
    public ProcessInstance getInstanceById(String instanceId) {
        ProcessInstance processInstance = processInstanceRepository.getInstanceById(instanceId);
        if (processInstance == null) {
            throw new NotFoundProcessInstanceException(instanceId);
        }
        return processInstance;
    }

    @Override
    public void save(ProcessInstance processInstance) {
        processInstanceRepository.save(processInstance);
    }

    @Override
    public <T extends AdvancedPagedQuery> List<ProcessInstance> list(T query) {
        return processInstanceRepository.list(query);
    }

    @Override
    public <T extends AdvancedPagedQuery> long count(T query) {
        return processInstanceRepository.count(query);
    }


    @Override
    public void deleteById(String instanceId) {
        processInstanceRepository.deleteById(instanceId);
    }


}
