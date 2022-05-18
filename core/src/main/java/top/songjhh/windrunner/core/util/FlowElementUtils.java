package top.songjhh.windrunner.core.util;

import top.songjhh.windrunner.core.engine.runtime.converter.FlowModelConvertProvider;
import top.songjhh.windrunner.core.engine.runtime.model.DefinitionFileType;
import top.songjhh.windrunner.core.engine.runtime.model.FlowElement;
import top.songjhh.windrunner.core.engine.runtime.model.StartEvent;
import top.songjhh.windrunner.core.exception.FlowElementCastException;
import top.songjhh.windrunner.core.exception.NotFoundStartEventException;

import java.util.Collection;

/**
 * @author songjhh
 */
public class FlowElementUtils {
    private FlowElementUtils() {
    }

    public static StartEvent getStartEvent(Collection<FlowElement> flowElementMap) {
        for (FlowElement flowElement : flowElementMap) {
            if (FlowElement.Type.START_EVENT.equals(flowElement.getType())) {
                if (!(flowElement instanceof StartEvent)) {
                    throw new FlowElementCastException(StartEvent.class);
                }
                return (StartEvent) flowElement;
            }
        }
        throw new NotFoundStartEventException();
    }

    public static StartEvent getStartEvent(String source, DefinitionFileType type) {
        return getStartEvent(FlowModelConvertProvider.converterToModel(source, type).getFlowElementList());
    }

}
