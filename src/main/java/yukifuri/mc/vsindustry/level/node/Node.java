package yukifuri.mc.vsindustry.level.node;

import yukifuri.mc.vsindustry.api.level.blockentity.BaseBlockEntity;

/**
 * Must be implemented by BaseBlockEntity and its children!
 */
public interface Node {
    default GridNode getGridNode() {
        return GridNode.of((BaseBlockEntity) this);
    }
}
