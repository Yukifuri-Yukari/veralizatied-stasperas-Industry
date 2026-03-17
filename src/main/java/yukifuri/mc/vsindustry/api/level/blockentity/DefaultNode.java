package yukifuri.mc.vsindustry.api.level.blockentity;

import net.minecraft.server.level.ServerLevel;
import yukifuri.mc.vsindustry.level.grid.GridManager;
import yukifuri.mc.vsindustry.level.node.Node;

public interface DefaultNode extends Node {
    default void defaultOnFirstTick(ServerLevel level) {
        var node = getGridNode();
        if (node.getGrid() == null)
            GridManager.get(level).nodeJoined(node);
        else
            node.online();
    }

    default void defaultOnChunkUnload() {
        getGridNode().offline();
    }
}
