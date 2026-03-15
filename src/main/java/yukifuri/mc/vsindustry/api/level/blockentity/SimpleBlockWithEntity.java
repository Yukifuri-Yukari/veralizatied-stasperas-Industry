package yukifuri.mc.vsindustry.api.level.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import yukifuri.mc.vsindustry.level.node.GridNode;
import yukifuri.mc.vsindustry.level.node.Node;

import java.util.Objects;

public abstract class SimpleBlockWithEntity<T extends BlockEntity> extends BaseEntityBlock implements BlockEntityTicker<T> {
    protected SimpleBlockWithEntity(Properties properties) {
        super(properties);
    }

    protected GridNode getNode(LevelAccessor accessor, BlockPos pos) {
        return ((Node) Objects.requireNonNull(accessor.getBlockEntity(pos))).getGridNode();
    }

    /// We are sure must be the current type when call it...
    @SuppressWarnings("unchecked")
    protected <E extends BaseBlockEntity> E getEntity(LevelAccessor accessor, BlockPos pos) {
        return (E) Objects.requireNonNull(accessor.getBlockEntity(pos));
    }
}
