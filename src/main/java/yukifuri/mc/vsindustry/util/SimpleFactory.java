package yukifuri.mc.vsindustry.util;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.world.item.Item;

@MethodsReturnNonnullByDefault
public class SimpleFactory {
    public static Item simpleItem() {
        return simpleItem(new Item.Properties());
    }

    public static Item simpleItem(Item.Properties prop) {
        return new Item(prop);
    }
}
