package top.songjhh.windrunner.core.engine.runtime.model;

/**
 * Created by @author songjhh
 */
public class ProcessDefinition {

    private String deploymentId;
    private String name;
    private DefinitionFileType type;
    private FlowModel flowModel;

    public FlowModel getFlowModel() {
        return flowModel;
    }

    public void setFlowModel(FlowModel flowModel) {
        this.flowModel = flowModel;
    }
}
