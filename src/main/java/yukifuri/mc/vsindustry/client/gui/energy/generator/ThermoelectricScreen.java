package yukifuri.mc.vsindustry.client.gui.energy.generator;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import yukifuri.mc.vsindustry.gui.api.UIScreen;
import yukifuri.mc.vsindustry.gui.ui.energy.generator.ThermoelectricUi;

import static yukifuri.mc.vsindustry.block.energy.generator.ThermoelectricGenerator.Entity.MAX_POWER;
import static yukifuri.mc.vsindustry.block.energy.generator.ThermoelectricGenerator.Entity.MAX_PROGRESS;

public class ThermoelectricScreen extends UIScreen<ThermoelectricUi> {
    public ThermoelectricScreen(ThermoelectricUi ui, Inventory inventory, Component title) {
        super(ui, inventory, title);
    }

    private int x, y;

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float delta) {
        x = (width - imageWidth) / 2;
        y = (height - imageHeight) / 2;

        renderBackground(graphics);
        super.render(graphics, mx, my, delta);
        graphics.drawString(font,
                "Power: " + ui.getPower() + " / " + MAX_POWER +
                        "; Progress: " + ui.getProgress() + " / " + MAX_PROGRESS,
                x + 10, y + 10, 0xFFFFFFFF
        );
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mx, int my) {
        super.renderBg(graphics, delta, mx, my);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics) {
        super.renderBackground(guiGraphics);
    }
}
