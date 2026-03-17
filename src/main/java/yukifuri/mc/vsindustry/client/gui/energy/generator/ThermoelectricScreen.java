package yukifuri.mc.vsindustry.client.gui.energy.generator;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import yukifuri.mc.vsindustry.gui.api.UIScreen;
import yukifuri.mc.vsindustry.gui.ui.energy.generator.ThermoelectricUi;

public class ThermoelectricScreen extends UIScreen<ThermoelectricUi> {
    public ThermoelectricScreen(ThermoelectricUi ui, Inventory inventory, Component title) {
        super(ui, inventory, title);
    }
}
