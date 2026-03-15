package yukifuri.mc.vsindustry.gui.api.widget;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class TextWidget extends Widget {
    public final int x;
    public final int y;
    public final Component message;
    public final int size;
    public final int color;
    public final Font font;

    public TextWidget(int x, int y, Component message, int size, int color, Font font) {
        this.x = x;
        this.y = y;
        this.message = message;
        this.size = size;
        this.color = color;
        this.font = font;
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.drawString(font, message, x, y, color);
    }
}
