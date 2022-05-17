package top.songjhh.windrunner.core.engine.runtime.converter;

import top.songjhh.windrunner.core.engine.runtime.model.FlowModel;

/**
 * @author songjhh
 */
public interface FlowModelConverter {

    FlowModel convertToModel(String source);

}
