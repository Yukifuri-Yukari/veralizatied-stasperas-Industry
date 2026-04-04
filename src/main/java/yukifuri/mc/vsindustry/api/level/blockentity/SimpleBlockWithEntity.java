package yukifuri.mc.vsindustry.api.level.blockentity;

import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;

public abstract class SimpleBlockWithEntity<T extends BlockEntity>
        extends BaseEntityBlock
        implements BlockEntityTicker<T> {
    protected SimpleBlockWithEntity(Properties properties) {
        super(properties);
    }
}
