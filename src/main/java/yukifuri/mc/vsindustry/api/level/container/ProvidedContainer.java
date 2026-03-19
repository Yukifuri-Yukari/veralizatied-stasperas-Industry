package yukifuri.mc.vsindustry.api.level.container;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

/**
 * A {@link Container} mixin interface that delegates all operations to an inner container
 * returned by {@link #getContainer()}.
 * <p>
 * Implementors only need to provide {@link #getContainer()} and {@link Container#setChanged()};
 * all other {@link Container} methods are forwarded automatically.
 * <p>
 * 将所有操作委托给 {@link #getContainer()} 返回的内部容器的 {@link Container} 混入接口.
 * <p>
 * 实现方只需提供 {@link #getContainer()} 与 {@link Container#setChanged()},
 * 其余 {@link Container} 方法均会自动转发.
 */
@MethodsReturnNonnullByDefault
public interface ProvidedContainer extends Container {
    /**
     * Returns the underlying container that all operations are delegated to.
     * <p>
     * 返回所有操作所委托的底层容器.
     */
    Container getContainer();

    /** Delegates to {@link Container#getContainerSize()}. <p> 委托至 {@link Container#getContainerSize()}. */
    default int getContainerSize() {
        return getContainer().getContainerSize();
    }

    /** Delegates to {@link Container#isEmpty()}. <p> 委托至 {@link Container#isEmpty()}. */
    default boolean isEmpty() {
        return getContainer().isEmpty();
    }

    /** Delegates to {@link Container#getItem(int)}. <p> 委托至 {@link Container#getItem(int)}. */
    default ItemStack getItem(int i) {
        return getContainer().getItem(i);
    }

    /** Delegates to {@link Container#removeItem(int, int)}. <p> 委托至 {@link Container#removeItem(int, int)}. */
    default ItemStack removeItem(int i, int j) {
        return getContainer().removeItem(i, j);
    }

    /** Delegates to {@link Container#removeItemNoUpdate(int)}. <p> 委托至 {@link Container#removeItemNoUpdate(int)}. */
    default ItemStack removeItemNoUpdate(int i) {
        return getContainer().removeItemNoUpdate(i);
    }

    /** Delegates to {@link Container#setItem(int, ItemStack)}. <p> 委托至 {@link Container#setItem(int, ItemStack)}. */
    default void setItem(int i, ItemStack itemStack) {
        getContainer().setItem(i, itemStack);
    }

    /** Delegates to {@link Container#clearContent()}. <p> 委托至 {@link Container#clearContent()}. */
    default void clearContent() {
        getContainer().clearContent();
    }
}
