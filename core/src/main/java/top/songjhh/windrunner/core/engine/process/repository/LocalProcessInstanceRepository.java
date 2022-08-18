package top.songjhh.windrunner.core.engine.process.repository;

import cc.ldsd.common.bean.web.AdvancedPagedQuery;
import top.songjhh.windrunner.core.engine.process.model.ProcessInstance;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author songjhh
 */
public class LocalProcessInstanceRepository implements ProcessInstanceRepository {

    private final Map<String, ProcessInstance> processInstanceMap;

    public LocalProcessInstanceRepository() {
        this.processInstanceMap = new LinkedHashMap<>();
    }

    @Override
    public void save(ProcessInstance processInstance) {
        processInstanceMap.put(processInstance.getInstanceId(), processInstance);
    }

    @Override
    public ProcessInstance getInstanceById(String instanceId) {
        return processInstanceMap.get(instanceId);
    }

    @Override
    public <T extends AdvancedPagedQuery> List<ProcessInstance> list(T query) {
        return new ArrayList<>();
    }

    @Override
    public <T extends AdvancedPagedQuery> long count(T query) {
        return 0;
    }

}
