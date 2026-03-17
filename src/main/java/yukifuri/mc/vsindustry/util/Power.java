package yukifuri.mc.vsindustry.util;

import net.minecraft.world.inventory.ContainerData;

public class Power {
    public static long from(ContainerData data, int high, int low) {
        return ((long) data.get(high) << 32) | (data.get(low) & 0xFFFFFFFFL);
    }

    public static long from(ContainerData data, int high) {
        return from(data, high, high + 1);
    }

    public static void to(ContainerData data, long power, int high, int low) {
        data.set(high, (int) (power >> 32));
        data.set(low, (int) power);
    }

    public static void to(ContainerData data, long power, int high) {
        to(data, power, high, high + 1);
    }
}
