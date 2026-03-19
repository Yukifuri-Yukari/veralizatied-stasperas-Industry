package yukifuri.mc.vsindustry.api.level.blockentity;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Base class for block entities that hold a container and can open a GUI menu.
 * <p>
 * Handles lock code serialization, custom naming, and delegates menu creation
 * to {@link #createMenu(int, Inventory)}.
 * <p>
 * 持有容器且可打开 GUI 菜单的方块实体基类.
 * <p>
 * 负责锁码序列化, 自定义命名处理, 并将菜单创建委托给 {@link #createMenu(int, Inventory)}.
 */
@MethodsReturnNonnullByDefault
public abstract class BaseContainerBlockEntity extends BaseBlockEntity implements Container, MenuProvider, Nameable {
    private LockCode lockKey = LockCode.NO_LOCK;
    @Nullable
    private Component name;

    protected BaseContainerBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState blockState) {
        super(type, pos, blockState);
    }

    /**
     * Loads lock code and custom name from NBT.
     * <p>
     * 从 NBT 中加载锁码与自定义名称.
     */
    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.lockKey = LockCode.fromTag(tag);
        if (tag.contains("CustomName", 8)) {
            this.name = Component.Serializer.fromJson(tag.getString("CustomName"));
        }
    }

    /**
     * Saves lock code and custom name to NBT.
     * <p>
     * 将锁码与自定义名称保存到 NBT.
     */
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        this.lockKey.addToTag(tag);
        if (this.name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(this.name));
        }
    }

    /**
     * Sets a custom display name for this container.
     * <p>
     * 为此容器设置自定义显示名称.
     */
    public void setCustomName(Component name) {
        this.name = name;
    }

    /**
     * Returns the custom name if set, otherwise returns {@link #getDefaultName()}.
     * <p>
     * 若已设置自定义名称则返回自定义名称, 否则返回 {@link #getDefaultName()}.
     */
    @Override
    public Component getName() {
        return this.name != null ? this.name : this.getDefaultName();
    }

    /** Returns the display name, same as {@link #getName()}. <p> 返回显示名称, 与 {@link #getName()} 相同. */
    @Override
    public Component getDisplayName() {
        return this.getName();
    }

    /** Returns the custom name, or {@code null} if none is set. <p> 返回自定义名称, 若未设置则为 {@code null}. */
    @Override @Nullable
    public Component getCustomName() {
        return this.name;
    }

    /**
     * Returns the default (untranslated) display name used when no custom name is set.
     * <p>
     * 返回未设置自定义名称时使用的默认显示名称.
     */
    protected abstract Component getDefaultName();

    /**
     * Returns whether the given player can open this container (checks lock code).
     * <p>
     * 返回指定玩家是否可以打开此容器(检查锁码).
     */
    public boolean canOpen(Player player) {
        return canUnlock(player, this.lockKey, this.getDisplayName());
    }

    /**
     * Checks whether the player can unlock the container with the given lock code.
     * Displays a locked message and plays a sound if access is denied.
     * <p>
     * 检查玩家是否可使用给定锁码解锁容器.
     * 若拒绝访问则向玩家显示锁定提示并播放音效.
     */
    public static boolean canUnlock(Player player, LockCode code, Component displayName) {
        if (!player.isSpectator() && !code.unlocksWith(player.getMainHandItem())) {
            player.displayClientMessage(Component.translatable("container.isLocked", displayName), true);
            player.playNotifySound(SoundEvents.CHEST_LOCKED, SoundSource.BLOCKS, 1.0F, 1.0F);
            return false;
        } else {
            return true;
        }
    }

    /**
     * Opens the menu for the given player if the container is unlocked.
     * Returns {@code null} if the player cannot open the container.
     * <p>
     * 若容器未上锁则为指定玩家打开菜单.
     * 若玩家无法打开则返回 {@code null}.
     */
    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return this.canOpen(player) ? this.createMenu(i, inventory) : null;
    }

    /**
     * Creates the container menu for the given sync ID and player inventory.
     * Called only after the lock check has passed.
     * <p>
     * 使用给定同步 ID 及玩家背包创建容器菜单.
     * 仅在锁码验证通过后调用.
     */
    protected abstract AbstractContainerMenu createMenu(int containerId, Inventory inventory);
}
