package yukifuri.mc.vsindustry.gui.ui.energy.generator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import yukifuri.mc.vsindustry.gui.api.UI;
import yukifuri.mc.vsindustry.gui.api.UIType;
import yukifuri.mc.vsindustry.network.NetworkProcessor;
import yukifuri.mc.vsindustry.network.packets.SyncGuiDataPacket;
import yukifuri.mc.vsindustry.util.Power;

public class ThermoelectricUi extends UI<ThermoelectricUi> {
    public static final Component TITLE = Component.translatable("block.vs_industry.thermoelectic_generator");
    public static final UIType<ThermoelectricUi> TYPE = UIType.of(ThermoelectricUi::createUI);

    public final Container container;
    public final ContainerData data;

    public final Slot fuelSlot;

    public ThermoelectricUi(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(1), new SimpleContainerData(3));
    }

    public ThermoelectricUi(int id, Inventory inventory, Container c, ContainerData dataC) {
        super(TYPE, TITLE, inventory, id);
        this.container = c;
        this.fuelSlot = new Slot(container, 0, 72, 37);
        this.data = dataC;
    }

    @Override
    public void init() {
        addInventorySlots(inv);
        addSlots(fuelSlot);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        NetworkProcessor.getInstance().sendToClient(
                new SyncGuiDataPacket(containerId, buf -> {
                    buf.writeItem(fuelSlot.getItem());
                    buf.writeInt(getProgress());
                    buf.writeLong(getPower());
                }), inv.player
        );
    }

    @Override
    public void syncDataOnClient(SyncGuiDataPacket packet) {
        var payload = packet.getPayload();
        fuelSlot.set(payload.readItem());
        setProgress(payload.readInt());
        setPower(payload.readLong());
    }

    public int getProgress() {
        return data.get(0);
    }

    public long getPower() {
        return Power.from(data, 1);
    }

    public void setProgress(int progress) {
        data.set(0, progress);
    }

    public void setPower(long power) {
        Power.to(data, power, 1);
    }

    public static ThermoelectricUi createUI(int syncId, Inventory inventory) {
        return new ThermoelectricUi(syncId, inventory);
    }
}
