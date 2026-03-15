package yukifuri.mc.vsindustry.api.level.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import yukifuri.mc.vsindustry.hook.TickHandler;
import yukifuri.mc.vsindustry.level.grid.GridManager;
import yukifuri.mc.vsindustry.level.node.Node;

public abstract class BaseBlockEntity extends BlockEntity {
    public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void onChunkUnload() { }

    public boolean isLoaded() {
        return hasLevel() && level.hasChunkAt(getBlockPos());
    }

    public long expectedPower() { return 0; }

    public long powerSupplied() { return 0; }

    public void powerAccepted(long power) {}

    public void scheduleInit() {
        var level = (ServerLevel) getLevel();
        if (level == null) return;
        TickHandler.getInstance().scheduleOnFirstTick(level, () -> onFirstTick(level));
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        scheduleInit();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level instanceof ServerLevel serverLevel) {
            onRemoved(serverLevel);
        }
    }

    protected void onFirstTick(ServerLevel level) { }

    protected void onRemoved(ServerLevel level) { }
}
