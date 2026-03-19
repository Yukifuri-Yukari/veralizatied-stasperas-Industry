package yukifuri.mc.vsindustry.logic.level.topology;

import yukifuri.mc.vsindustry.logic.level.node.GridNode;

public interface NodeVisitor {
    /**
     * Called for each node in the topology.
     * @return true to continue visiting, false to stop
     */
    boolean visit(GridNode node);
}
