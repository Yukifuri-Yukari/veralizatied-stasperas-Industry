package yukifuri.mc.vsindustry.logic.level.network.base;

import net.minecraft.server.level.ServerLevel;
import yukifuri.mc.vsindustry.VSIndustry;

public interface BaseEntityNode<Node extends BaseNetworkNode<?>> {
    Node getNode();

    BaseNetworkManager<?, Node> getManager(ServerLevel level);

    default void defaultOnFirstTick(ServerLevel level) {
        VSIndustry.LOGGER.info("[Network {}] Node loaded: {} at {}", getNode().getNetwork(), getNode(), level);
        var node = getNode();
        // 若 firstTick 执行时区块已卸载（onceTicker 找不到节点→未能 offline），不要重新上线
        if (!node.getOwner().isLoaded()) return;
        if (node.getNetwork() == null)
            getManager(level).nodeJoined(node);
        else
            node.online();
    }

    default void defaultOnChunkUnload(ServerLevel level) {
        VSIndustry.LOGGER.info("[Network {}] Node Unloaded: {} at {}", getNode().getNetwork(), getNode(), level);
        var registered = getManager(level).getNodeAt(getNode().getPos());
        if (registered != null) registered.offline();
    }

    default void defaultOnRemoved(ServerLevel level) {
        getManager(level).nodeRemoved(getNode());
    }
}
