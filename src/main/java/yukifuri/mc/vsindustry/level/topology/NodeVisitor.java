package yukifuri.mc.vsindustry.level.topology;

import yukifuri.mc.vsindustry.level.node.GridNode;

public interface NodeVisitor {
    /**
     * Called for each node in the topology.
     * @return true to continue visiting, false to stop
     */
    boolean visit(GridNode node);
}
