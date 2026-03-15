package yukifuri.mc.vsindustry.api.level.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

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

    public void scheduleInit() { }
}
