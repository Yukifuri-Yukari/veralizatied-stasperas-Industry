package yukifuri.mc.vsindustry.api.level.blockentity;

import net.minecraft.server.level.ServerLevel;
import yukifuri.mc.vsindustry.logic.level.grid.GridManager;
import yukifuri.mc.vsindustry.logic.level.node.Node;

/**
 * Mixin interface that provides default grid lifecycle implementations for block entities.
 * <p>
 * Implement this on a {@link yukifuri.mc.vsindustry.api.level.blockentity.BaseBlockEntity}
 * subclass and delegate the corresponding lifecycle hooks to these defaults.
 * <p>
 * 为方块实体提供默认电力网格生命周期实现的混入接口.
 * <p>
 * 在 {@link yukifuri.mc.vsindustry.api.level.blockentity.BaseBlockEntity} 子类上实现此接口,
 * 并将对应的生命周期钩子委托给这些默认实现.
 */
public interface DefaultNode extends Node {
    /**
     * Default implementation for {@code onFirstTick}.
     * Joins the grid for the first time if not yet assigned, or brings the node back online.
     * <p>
     * {@code onFirstTick} 的默认实现.
     * 若尚未分配网格则首次加入, 否则将节点重新设为在线.
     */
    default void defaultOnFirstTick(ServerLevel level) {
        var node = getGridNode();
        if (node.getGrid() == null)
            GridManager.get(level).nodeJoined(node);
        else
            node.online();
    }

    /**
     * Default implementation for {@code onChunkUnload}.
     * Marks the grid node as offline without removing it from the grid.
     * <p>
     * {@code onChunkUnload} 的默认实现.
     * 将网格节点标记为离线, 但不将其从网格中移除.
     */
    default void defaultOnChunkUnload() {
        getGridNode().offline();
    }

    /**
     * Default implementation for {@code onRemoved}.
     * Permanently removes the node from its grid and triggers grid split if necessary.
     * <p>
     * {@code onRemoved} 的默认实现.
     * 将节点从其网格中永久移除, 必要时触发网格分裂.
     */
    default void defaultOnRemoved(ServerLevel level) {
        GridManager.get(level).nodeRemoved(getGridNode());
    }
}
