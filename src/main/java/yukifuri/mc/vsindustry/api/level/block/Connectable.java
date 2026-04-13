package yukifuri.mc.vsindustry.api.level.block;

import yukifuri.mc.vsindustry.logic.level.network.base.ConnectableType;

/**
 * Marker interface for blocks that can form connections with neighbouring blocks
 * (e.g. cables connecting to machines).
 * <p>
 * 标记接口, 表示方块可与相邻方块建立连接(如线缆连接机器).
 */
public interface Connectable {
    default boolean isConnectable(ConnectableType type) {
        return type == ConnectableType.Power;
    }
}
