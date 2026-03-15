package yukifuri.mc.vsindustry.api.level.container;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

@MethodsReturnNonnullByDefault
public interface ProvidedContainer extends Container {
    Container getContainer();

    default int getContainerSize() {
        return getContainer().getContainerSize();
    }

    default boolean isEmpty() {
        return getContainer().isEmpty();
    }

    default ItemStack getItem(int i) {
        return getContainer().getItem(i);
    }

    default ItemStack removeItem(int i, int j) {
        return getContainer().removeItem(i, j);
    }

    default ItemStack removeItemNoUpdate(int i) {
        return getContainer().removeItemNoUpdate(i);
    }

    default void setItem(int i, ItemStack itemStack) {
        getContainer().setItem(i, itemStack);
    }

    default void clearContent() {
        getContainer().clearContent();
    }
}
