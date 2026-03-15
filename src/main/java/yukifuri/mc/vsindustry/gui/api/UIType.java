package yukifuri.mc.vsindustry.gui.api;

import com.mojang.datafixers.util.Function3;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.MenuType;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

@MethodsReturnNonnullByDefault
public interface UIType<T extends UI<T>> {
    MenuType<T> get();

    static <T extends UI<T>> UIType<T> of(
            BiFunction<Integer, Inventory, T> menuSupplier
    ) {
        var type = new MenuType<>(menuSupplier::apply, FeatureFlags.DEFAULT_FLAGS);
        return () -> type;
    }
}
