package top.songjhh.windrunner.core.engine.runtime.model;

import lombok.Getter;

/**
 * Created by @author songjhh
 */
@Getter
public class StartEvent extends FlowEvent {

    private String formKey;

    @Override
    public Type getType() {
        return Type.START_EVENT;
    }

}
