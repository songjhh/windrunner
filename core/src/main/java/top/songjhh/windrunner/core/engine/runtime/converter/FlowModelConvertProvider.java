package top.songjhh.windrunner.core.engine.runtime.converter;

import top.songjhh.windrunner.core.engine.runtime.model.DefinitionFileType;
import top.songjhh.windrunner.core.engine.runtime.model.FlowModel;
import top.songjhh.windrunner.core.exception.FlowConverterException;

/**
 * @author songjhh
 */
public class FlowModelConvertProvider {

    private FlowModelConvertProvider() {
    }

    public static FlowModel converterToModel(String source, DefinitionFileType type) {
        if (DefinitionFileType.WIND_RUNNER_JSON.equals(type)) {
            return WindRunnerJsonFlowModelConverter.convertToModel(source);
        }
        throw new FlowConverterException(type);
    }

}
