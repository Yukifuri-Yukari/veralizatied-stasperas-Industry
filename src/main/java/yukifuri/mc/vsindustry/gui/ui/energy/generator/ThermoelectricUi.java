package yukifuri.mc.vsindustry.gui.ui.energy.generator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import yukifuri.mc.vsindustry.gui.api.UI;
import yukifuri.mc.vsindustry.gui.api.UIType;

public class ThermoelectricUi extends UI<ThermoelectricUi> {
    public static final Component TITLE = Component.translatable("block.vs_industry.thermoelectic_generator");
    public static final UIType<ThermoelectricUi> TYPE = UIType.of(ThermoelectricUi::createUI);

    public final Container container;
    public final ContainerData data;

    public ThermoelectricUi(int id, Inventory inventory) {
        this(id, inventory, new SimpleContainer(1), new SimpleContainerData(3));
    }

    public ThermoelectricUi(int id, Inventory inventory, Container c, ContainerData dataC) {
        super(TYPE, TITLE, inventory, id);
        this.container = c;
        this.data = dataC;
    }

    public static ThermoelectricUi createUI(int syncId, Inventory inventory) {
        return new ThermoelectricUi(syncId, inventory);
    }
}
