package top.songjhh.windrunner.core.engine.runtime.model;

import lombok.Getter;

/**
 * Created by @author songjhh
 */
@Getter
public class SequenceFlow extends FlowElement {

    private String sourceRef;
    private String targetRef;
    private String conditionExpression;

    @Override
    public Type getType() {
        return Type.SEQUENCE_FLOW;
    }

    public void cleanCondition() {
        this.conditionExpression = null;
    }

}
