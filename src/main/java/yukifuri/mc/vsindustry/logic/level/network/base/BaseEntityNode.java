package yukifuri.mc.vsindustry.logic.level.network.base;

import net.minecraft.server.level.ServerLevel;

public interface BaseEntityNode<Node extends BaseNetworkNode<?>> {
    Node getNode();

    BaseNetworkManager<?, Node> getManager(ServerLevel level);

    default void defaultOnFirstTick(ServerLevel level) {
        var node = getNode();
        if (node.getNetwork() == null)
            getManager(level).nodeJoined(node);
        else
            node.online();
    }

    default void defaultOnChunkUnload() {
        getNode().offline();
    }

    default void defaultOnRemoved(ServerLevel level) {
        getManager(level).nodeRemoved(getNode());
    }
}
