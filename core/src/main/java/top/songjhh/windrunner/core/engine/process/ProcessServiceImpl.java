package top.songjhh.windrunner.core.engine.process;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.deployment.model.Deployment;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstanceBuilder;
import top.songjhh.windrunner.core.engine.process.repository.ProcessInstanceRepository;
import top.songjhh.windrunner.core.engine.runtime.model.UserEntity;

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
                .setSource(deployment.getSource())
                .setType(deployment.getType())
                .setVariables(variables)
                .setStarter(starter)
                .build();
        processInstanceRepository.save(processInstance);
        return processInstance;
    }

    @Override
    public ProcessInstance getInstanceById(String instanceId) {
        return processInstanceRepository.getInstanceById(instanceId);
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

}
