package yukifuri.mc.vsindustry.api.level.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import yukifuri.mc.vsindustry.logic.hook.TickHandler;

/**
 * Base class for all block entities in vs_industry.
 * <p>
 * Provides the deferred first-tick init pattern and power grid integration hooks.
 * Subclasses override {@link #onFirstTick}, {@link #onChunkUnload}, and {@link #onRemoved}
 * to participate in the power grid lifecycle.
 * <p>
 * vs_industry 中所有方块实体的基类.
 * <p>
 * 提供延迟首 tick 初始化模式及电力网格集成钩子.
 * 子类通过覆写 {@link #onFirstTick}, {@link #onChunkUnload} 及 {@link #onRemoved}
 * 来参与电力网格的生命周期管理.
 */
public abstract class BaseBlockEntity extends BlockEntity {
    public BaseBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    /**
     * Called when the chunk containing this block entity is unloaded.
     * Override to handle grid node offline logic.
     * <p>
     * 当包含此方块实体的区块被卸载时调用.
     * 覆写此方法以处理网格节点下线逻辑.
     */
    public void onChunkUnload() { }

    /**
     * Returns whether this block entity's chunk is currently loaded in the level.
     * <p>
     * 返回此方块实体所在区块是否已在世界中加载.
     */
    public boolean isLoaded() {
        return hasLevel() && level.hasChunkAt(getBlockPos());
    }

    /**
     * Returns the amount of power this block entity expects to receive per tick.
     * Defaults to {@code 0} (no demand).
     * <p>
     * 返回此方块实体每 tick 期望接收的电力量.
     * 默认为 {@code 0}(无需求).
     */
    public long expectedPower() { return 0; }

    /**
     * Returns the maximum amount of power this block entity can supply per tick.
     * Defaults to {@code 0} (no supply).
     * <p>
     * 返回此方块实体每 tick 最多可提供的电力量.
     * 默认为 {@code 0}(无供给).
     */
    public long powerSuppliable() { return 0; }

    /**
     * Called by the power grid after distributing power to notify how much was accepted.
     * <p>
     * 电力网格分配完毕后调用, 通知此方块实体实际接收到的电力量.
     *
     * @param power the amount of power accepted / 实际接收的电力量
     */
    public void powerAccepted(long power) {}

    /**
     * Called to notify how much power was consumed this tick.
     * <p>
     * 通知本 tick 消耗的电力量.
     *
     * @param amount the amount consumed / 消耗的电力量
     */
    public void powerConsumed(long amount) {}

    /**
     * Schedules {@link #onFirstTick} to run at the start of the next server tick.
     * No-ops on the client side or before the level is available.
     * <p>
     * 将 {@link #onFirstTick} 安排在下一服务端 tick 开始时执行.
     * 在客户端或 level 尚不可用时为空操作.
     */
    public void scheduleInit() {
        if (level == null || level.isClientSide())
            return;
        ServerLevel level = (ServerLevel) getLevel();
        TickHandler.getInstance().scheduleOnFirstTick(level, () -> onFirstTick(level));
    }

    /**
     * Called when the block entity is re-added to the level (e.g. chunk load or placement).
     * Triggers {@link #scheduleInit} to defer first-tick logic.
     * <p>
     * 方块实体重新加入世界时调用(如区块加载或放置).
     * 触发 {@link #scheduleInit} 以延迟首 tick 逻辑.
     */
    @Override
    public void clearRemoved() {
        super.clearRemoved();
        scheduleInit();
    }

    /**
     * Called when the block entity is removed from the level.
     * Delegates to {@link #onRemoved} on the server side.
     * <p>
     * 方块实体从世界中移除时调用.
     * 在服务端委托 {@link #onRemoved} 处理.
     */
    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level instanceof ServerLevel serverLevel) {
            onRemoved(serverLevel);
        }
    }

    /**
     * Server-side callback invoked on the first tick after the block entity is placed or loaded.
     * Override to perform grid join or reconnection logic.
     * <p>
     * 方块实体放置或加载后首个 tick 时在服务端调用.
     * 覆写以执行加入网格或重连逻辑.
     */
    protected void onFirstTick(ServerLevel level) { }

    /**
     * Server-side callback invoked when the block entity is permanently removed (e.g. block broken).
     * Override to perform grid leave logic.
     * <p>
     * 方块实体被永久移除时(如方块被破坏)在服务端调用.
     * 覆写以执行离开网格逻辑.
     */
    protected void onRemoved(ServerLevel level) { }
}
