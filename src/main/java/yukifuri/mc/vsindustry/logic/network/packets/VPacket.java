package yukifuri.mc.vsindustry.logic.network.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import yukifuri.mc.vsindustry.VSIndustry;

public abstract class VPacket {
    public static final ResourceLocation CHANNEL_ID = new ResourceLocation(VSIndustry.MOD_ID, "chl");

    protected FriendlyByteBuf buf;

    public final int getId() {
        return VPacketType.get(this.getClass()).ordinal();
    }

    public void receiveByServer(ServerPlayer player) {
        throw new UnsupportedOperationException("Packet " + getId() + " doesn't support server-side handling");
    }

    public void receiveByClient(Player player) {
        throw new UnsupportedOperationException("Packet " + getId() + " doesn't support client-side handling");
    }

    public void writeBytes(FriendlyByteBuf buf) {
        buf.capacity(buf.readableBytes());
        this.buf = buf;
    }

    public FriendlyByteBuf getBytes() {
        if (buf.readableBytes() > 2 * 1024 * 1024)
            throw new IllegalStateException("[413]" + getId() + ": Network Packet too large :(");

        return buf;
    }
}
