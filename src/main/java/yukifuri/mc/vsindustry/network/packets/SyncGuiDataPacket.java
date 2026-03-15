package yukifuri.mc.vsindustry.network.packets;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import yukifuri.mc.vsindustry.gui.api.UI;

import java.util.function.Consumer;

public class SyncGuiDataPacket extends VPacket {
    private final int containerId;

    public SyncGuiDataPacket(FriendlyByteBuf buf) {
        containerId = buf.readVarInt();
        this.buf = new FriendlyByteBuf(buf.copy());
    }

    public SyncGuiDataPacket(int containerId, Consumer<FriendlyByteBuf> payloads) {
        this.containerId = 0;

        var buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeInt(getId());
        buf.writeVarInt(containerId);
        payloads.accept(buf);
        writeBytes(buf);
    }

    public FriendlyByteBuf getPayload() {
        return buf;
    }

    @Override
    public void receiveByClient(Player player) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu instanceof UI<?> ui && ui.containerId == containerId) {
            ui.syncDataOnClient(this);
        }
    }

    @Override
    public void receiveByServer(ServerPlayer player) {
        AbstractContainerMenu menu = player.containerMenu;
        if (menu instanceof UI<?> ui && ui.containerId == containerId) {
            ui.getHandler().receiveClientAction(this);
        }
    }
}
