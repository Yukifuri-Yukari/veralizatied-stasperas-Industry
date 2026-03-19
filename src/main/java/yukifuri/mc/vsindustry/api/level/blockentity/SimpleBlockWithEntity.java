package yukifuri.mc.vsindustry.api.level.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import yukifuri.mc.vsindustry.logic.level.node.GridNode;
import yukifuri.mc.vsindustry.logic.level.node.Node;

import java.util.Objects;

/**
 * Convenience base class for blocks that own a block entity and implement a ticker.
 * <p>
 * Combines {@link BaseEntityBlock} and {@link BlockEntityTicker} into one type,
 * and provides helpers to retrieve the associated {@link BaseBlockEntity} or {@link GridNode}.
 * <p>
 * 拥有方块实体并实现 ticker 的方块便利基类.
 * <p>
 * 将 {@link BaseEntityBlock} 与 {@link BlockEntityTicker} 合并为同一类型,
 * 并提供获取关联 {@link BaseBlockEntity} 或 {@link GridNode} 的辅助方法.
 */
public abstract class SimpleBlockWithEntity<T extends BlockEntity> extends BaseEntityBlock implements BlockEntityTicker<T> {
    protected SimpleBlockWithEntity(Properties properties) {
        super(properties);
    }

    /**
     * Returns the {@link GridNode} of the block entity at the given position.
     * The block entity must implement {@link Node}.
     * <p>
     * 返回指定位置方块实体的 {@link GridNode}.
     * 该方块实体必须实现 {@link Node}.
     */
    protected GridNode getNode(LevelAccessor accessor, BlockPos pos) {
        return ((Node) Objects.requireNonNull(accessor.getBlockEntity(pos))).getGridNode();
    }

    /**
     * Returns the block entity at the given position cast to the expected type {@code E}.
     * The caller must ensure the block entity is of type {@code E}.
     * <p>
     * 将指定位置的方块实体强转为预期类型 {@code E} 并返回.
     * 调用方需确保该方块实体确实为 {@code E} 类型.
     */
    @SuppressWarnings("unchecked")
    protected <E extends BaseBlockEntity> E getEntity(LevelAccessor accessor, BlockPos pos) {
        return (E) Objects.requireNonNull(accessor.getBlockEntity(pos));
    }
}
