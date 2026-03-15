package yukifuri.mc.vsindustry.gui.api.widget;

import net.minecraft.client.gui.GuiGraphics;

public abstract class Widget {
    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
}