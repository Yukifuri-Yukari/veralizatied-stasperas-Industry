package yukifuri.mc.vsindustry.ui.energy.generator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import yukifuri.mc.vsindustry.api.gui.UI;
import yukifuri.mc.vsindustry.api.gui.UIType;
import yukifuri.mc.vsindustry.logic.network.NetworkProcessor;
import yukifuri.mc.vsindustry.logic.network.packets.SyncGuiDataPacket;
import yukifuri.mc.vsindustry.util.Power;

public class ThermoelectricUi extends UI<ThermoelectricUi> {
    public static final Component TITLE = Component.translatable("block.vs_industry.thermoelectric_generator");
    public static final UIType<ThermoelectricUi> TYPE = UIType.of(ThermoelectricUi::createUI);

    public final Container container;
    public final ContainerData data;

    public final Slot fuelSlot;

    private ItemStack lastFuel = ItemStack.EMPTY;

    public ThermoelectricUi(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(1), new SimpleContainerData(3));
    }

    public ThermoelectricUi(int id, Inventory inventory, Container c, ContainerData dataC) {
        super(TYPE, TITLE, inventory, id);
        this.container = c;
        this.fuelSlot = new Slot(container, 0, 72, 37);
        this.data = dataC;

        init();
    }

    @Override
    public void init() {
        addInventorySlots(inv);
        addSlot(fuelSlot);
        addDataSlots(data);
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        ItemStack fuel = fuelSlot.getItem();

        if (ItemStack.matches(fuel, lastFuel))
            return;

        lastFuel = fuel.copy();
        NetworkProcessor.getInstance().sendToClient(
                new SyncGuiDataPacket(containerId, buf -> buf.writeItem(fuelSlot.getItem())), inv.player
        );
    }


    @Override
    public void syncDataOnClient(SyncGuiDataPacket packet) {
        var payload = packet.getPayload();
        fuelSlot.set(payload.readItem());
    }

    public int getProgress() {
        return data.get(0);
    }

    public long getPower() {
        return Power.from(data, 1);
    }

    public static ThermoelectricUi createUI(int syncId, Inventory inventory) {
        return new ThermoelectricUi(syncId, inventory);
    }
}
