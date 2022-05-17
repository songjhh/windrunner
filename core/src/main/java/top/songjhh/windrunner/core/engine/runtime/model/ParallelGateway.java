package top.songjhh.windrunner.core.engine.runtime.model;

/**
 * @author songjhh
 */
public class ParallelGateway extends FlowGateway {
    @Override
    public Type getType() {
        return Type.PARALLEL_GATEWAY;
    }
}
