package top.songjhh.windrunner.core.exception;

import top.songjhh.windrunner.core.engine.runtime.model.DefinitionFileType;

/**
 * @author songjhh
 */
public class FlowConverterException extends ProcessEngineException {

    public FlowConverterException(DefinitionFileType type) {
        super("Can not find a right converter for " + type.toString());
    }

}
