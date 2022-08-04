package top.songjhh.windrunner.core.engine.runtime.model;

import lombok.Getter;

/**
 * Created by @author songjhh
 */
@Getter
public abstract class FlowActivity extends FlowNode {

    private String activityKey;
    private MultiInstanceLoopCharacteristics multiInstanceLoopCharacteristics;

    public enum MultiInstanceLoopCharacteristics {
        NONE, PARALLEL, SEQUENTIAL
    }
}
