package top.songjhh.windrunner.core.util;

import top.songjhh.windrunner.core.engine.runtime.converter.FlowModelConvertProvider;
import top.songjhh.windrunner.core.engine.runtime.model.DefinitionFileType;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.StartEvent;
import top.songjhh.windrunner.core.exception.FlowElementCastException;
import top.songjhh.windrunner.core.exception.NotFoundStartEventException;

/**
 * @author songjhh
 */
public class FlowElementUtils {
    private FlowElementUtils() {
    }

    public static StartEvent getStartEvent(String source, DefinitionFileType type) {
        for (FlowElement flowElement : FlowModelConvertProvider.converterToModel(source, type).getFlowElementList()) {
            if (FlowElement.Type.START_EVENT.equals(flowElement.getType())) {
                if (!(flowElement instanceof StartEvent)) {
                    throw new FlowElementCastException(StartEvent.class);
                }
                return (StartEvent) flowElement;
            }
        }
        throw new NotFoundStartEventException();
    }
}
