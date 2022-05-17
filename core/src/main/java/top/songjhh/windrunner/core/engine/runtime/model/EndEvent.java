package top.songjhh.windrunner.core.engine.runtime.model;

/**
 * Created by @author songjhh
 */
public class EndEvent extends FlowEvent {

    @Override
    public Type getType() {
        return Type.END_EVENT;
    }

}
