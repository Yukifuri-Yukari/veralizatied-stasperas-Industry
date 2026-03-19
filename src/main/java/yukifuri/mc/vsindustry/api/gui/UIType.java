package yukifuri.mc.vsindustry.api.gui;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;

import java.util.function.BiFunction;

/**
 * A thin wrapper around {@link MenuType} that binds a {@link UI} subclass to its factory.
 * <p>
 * Use {@link #of(BiFunction)} to create an instance from a two-argument constructor reference,
 * then register the result in {@code VGuis}.
 * <p>
 * 将 {@link UI} 子类与其工厂函数绑定的 {@link MenuType} 薄包装.
 * <p>
 * 使用 {@link #of(BiFunction)} 从双参数构造方法引用创建实例,
 * 然后在 {@code VGuis} 中注册结果.
 */
@MethodsReturnNonnullByDefault
public interface UIType<T extends UI<T>> {
    /**
     * Returns the underlying {@link MenuType} registered with Minecraft.
     * <p>
     * 返回向 Minecraft 注册的底层 {@link MenuType}.
     */
    MenuType<T> get();

    /**
     * Creates a new {@link UIType} from a {@code (syncId, inventory) -> T} factory function.
     * <p>
     * 从 {@code (syncId, inventory) -> T} 工厂函数创建新的 {@link UIType}.
     *
     * @param menuSupplier factory that constructs the menu given a sync ID and player inventory /
     *                     根据同步 ID 与玩家背包构造菜单的工厂函数
     */
    static <T extends UI<T>> UIType<T> of(
            BiFunction<Integer, Inventory, T> menuSupplier
    ) {
        var type = new MenuType<>(menuSupplier::apply, FeatureFlags.DEFAULT_FLAGS);
        return () -> type;
    }
}
