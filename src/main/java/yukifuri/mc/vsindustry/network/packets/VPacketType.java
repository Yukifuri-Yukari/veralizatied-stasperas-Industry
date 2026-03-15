package yukifuri.mc.vsindustry.network.packets;

import net.minecraft.network.FriendlyByteBuf;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public enum VPacketType {
    SYNC_GUI_DATA(SyncGuiDataPacket.class, SyncGuiDataPacket::new),
    ;

    public final Class<? extends VPacket> clazz;
    public final Function<FriendlyByteBuf, VPacket> factory;

    VPacketType(Class<? extends VPacket> clazz, Function<FriendlyByteBuf, VPacket> factory) {
        this.clazz = clazz;
        this.factory = factory;
        FactoryMap.MAP.put(clazz, this);
    }

    public static VPacketType get(Class<? extends VPacket> cls) {
        return FactoryMap.MAP.get(cls);
    }

    public static class FactoryMap {
        public static final Map<Class<? extends VPacket>, VPacketType> MAP = new HashMap<>();
    }
}
