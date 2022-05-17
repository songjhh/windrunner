package top.songjhh.windrunner.core.engine.runtime.model;

/**
 * @author songjhh
 */
public class ExclusiveGateway extends FlowGateway {

    @Override
    public Type getType() {
        return Type.EXCLUSIVE_GATEWAY;
    }

}
