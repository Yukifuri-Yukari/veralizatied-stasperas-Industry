package yukifuri.mc.vsindustry.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;
import yukifuri.mc.vsindustry.network.packets.VPacket;
import yukifuri.mc.vsindustry.network.packets.VPacketType;

public interface NetworkProcessor {
    static NetworkProcessor getInstance() {
        return Holder.instance;
    }

    void sendToServer(VPacket packet);
    void sendToClient(VPacket packet, Player player);

    class ServerProcessor implements NetworkProcessor {
        @Override
        public void sendToServer(VPacket packet) {
            throw new IllegalStateException("Server cannot send packets to server");
        }

        @Override
        public void sendToClient(VPacket packet, Player player) {
            var p = (ServerPlayer) player;
            ServerPlayNetworking.send(p, VPacket.CHANNEL_ID, packet.getBytes());
        }

        public void handle(
                MinecraftServer server,
                ServerPlayer player,
                ServerGamePacketListenerImpl handler,
                FriendlyByteBuf buf,
                PacketSender sender
        ) {
            final int id = buf.readInt();
            final VPacket packet = VPacketType.values()[id].factory.apply(buf);

            server.execute(() -> packet.receiveByServer(player));
        }
    }

    class ClientProcessor implements NetworkProcessor {

        @Override
        public void sendToServer(VPacket packet) {
            ClientPlayNetworking.send(VPacket.CHANNEL_ID, packet.getBytes());
        }

        @Override
        public void sendToClient(VPacket packet, Player player) {
            throw new IllegalStateException("Client cannot send packets to client");
        }

        public void handle(
                Minecraft mc,
                ClientPacketListener handler,
                FriendlyByteBuf buf,
                PacketSender sender
        ) {
            final int id = buf.readInt();
            final VPacket packet = VPacketType.values()[id].factory.apply(buf);

            mc.submit(() -> packet.receiveByClient(mc.player));
        }
    }

    class Holder {
        public static NetworkProcessor instance;
    }
}
