package yukifuri.mc.vsindustry.api.level.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import yukifuri.mc.vsindustry.hook.TickHandler;

public abstract class BaseBlockEntity extends BlockEntity {
    public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    public void onChunkUnload() { }

    public boolean isLoaded() {
        return hasLevel() && level.hasChunkAt(getBlockPos());
    }

    public long expectedPower() { return 0; }

    public long powerSuppliable() { return 0; }

    public void powerAccepted(long power) {}

    public void powerConsumed(long amount) {}

    public void scheduleInit() {
        if (level == null || level.isClientSide())
            return;
        ServerLevel level = (ServerLevel) getLevel();
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
