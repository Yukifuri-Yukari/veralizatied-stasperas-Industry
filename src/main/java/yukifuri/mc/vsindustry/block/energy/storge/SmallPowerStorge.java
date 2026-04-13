package yukifuri.mc.vsindustry.block.energy.storge;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import yukifuri.mc.vsindustry.api.level.block.Connectable;
import yukifuri.mc.vsindustry.api.level.blockentity.BaseBlockEntity;
import yukifuri.mc.vsindustry.api.level.blockentity.SimpleBlockWithEntity;
import yukifuri.mc.vsindustry.logic.level.network.power.DefaultPowerNetworkNode;
import yukifuri.mc.vsindustry.logic.level.network.power.PowerNetworkNode;
import yukifuri.mc.vsindustry.registries.VBlocks;

public class SmallPowerStorge extends SimpleBlockWithEntity<SmallPowerStorge.Entity> implements Connectable {
    public SmallPowerStorge() {
        super(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new Entity(blockPos, blockState);
    }

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState blockState,
            BlockEntityType<T> type
    ) {
        return level.isClientSide() ? null : createTickerHelper(type, Entity.TYPE, this);
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState state, Entity entity) {
        if (!entity.charging && !entity.consumedThisTick) entity.nextCharging = true;
        entity.charging = entity.nextCharging || entity.stored == 0;
        entity.nextCharging = false;
        entity.consumedThisTick = false;
    }

    public static class Entity extends BaseBlockEntity implements DefaultPowerNetworkNode {
        public static final BlockEntityType<Entity> TYPE = FabricBlockEntityTypeBuilder
                .create(Entity::new, VBlocks.SMALL_POWER_STORGE)
                .build();

        public static final long MAX_STORED = 100_000L;
        public static final long CHARGE_RATE = 2_000L;
        public static final long DISCHARGE_RATE = 2_000L;

        private long stored = 0;
        /// true = 充电模式（从网络吸收），false = 放电模式（向网络供电）
        private boolean charging = true;
        /// 由 powerAccepted/powerConsumed 回调写入，下一 tick 的 onTick() 读取并应用
        private boolean nextCharging = false;
        private boolean consumedThisTick = false;

        public Entity(BlockPos pos, BlockState blockState) {
            super(TYPE, pos, blockState);
        }

        //region NBT
        private static final String NBT_STORED = "Stored";
        private static final String NBT_CHARGING = "Charging";

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            if (stored > 0) tag.putLong(NBT_STORED, stored);
            tag.putBoolean(NBT_CHARGING, charging);
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            stored = tag.getLong(NBT_STORED);
            charging = !tag.contains(NBT_CHARGING) || tag.getBoolean(NBT_CHARGING);
        }
        //endregion

        //region Power
        @Override
        public long expectedPower() {
            if (!charging) return 0;
            return Math.min(CHARGE_RATE, MAX_STORED - stored);
        }

        @Override
        public long powerSuppliable() {
            if (charging) return 0;
            return Math.min(DISCHARGE_RATE, stored);
        }

        @Override
        public void powerAccepted(long power) {
            stored = Math.min(MAX_STORED, stored + power);
            // 收到电力说明电网有盈余，保持充电模式
            nextCharging = power > 0;
        }

        @Override
        public void powerConsumed(long amount) {
            long suppliable = Math.min(DISCHARGE_RATE, stored);
            stored = Math.max(0, stored - amount);
            consumedThisTick = true;
            // 网络未取走全部可供电量说明电网有盈余，切换为充电模式
            if (amount < suppliable) nextCharging = true;
        }
        //endregion

        //region Grid
        private PowerNetworkNode node;
        @Override
        public PowerNetworkNode getNode() {
            if (node == null) node = PowerNetworkNode.of(this);
            return node;
        }

        @Override
        protected void onFirstTick(ServerLevel level) {
            defaultOnFirstTick(level);
        }

        @Override
        protected void onRemoved(ServerLevel level) {
            defaultOnRemoved(level);
        }

        @Override
        public void onChunkUnload() {
            if (level instanceof ServerLevel sl) defaultOnChunkUnload(sl);
        }
        //endregion
    }
}
