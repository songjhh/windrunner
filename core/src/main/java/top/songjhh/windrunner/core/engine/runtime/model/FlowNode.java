package top.songjhh.windrunner.core.engine.runtime.model;

import lombok.Getter;

import java.util.List;

/**
 * Created by @author songjhh
 */
@Getter
public abstract class FlowNode extends FlowElement {

    private List<String> incoming;
    private List<String> outgoing;

}
