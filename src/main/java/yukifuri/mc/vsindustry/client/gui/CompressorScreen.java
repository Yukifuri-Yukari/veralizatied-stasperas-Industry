package yukifuri.mc.vsindustry.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import yukifuri.mc.vsindustry.gui.api.UI;
import yukifuri.mc.vsindustry.gui.api.UIScreen;
import yukifuri.mc.vsindustry.gui.ui.CompressorUi;
import yukifuri.mc.vsindustry.util.Power;

import static yukifuri.mc.vsindustry.block.Compressor.Entity.MAX_POWER;

public class CompressorScreen extends UIScreen<CompressorUi> {
    public static final ResourceLocation TEXTURE =
            new ResourceLocation("vs_industry:textures/gui/compressor.png");

    public CompressorScreen(CompressorUi ui, Inventory inventory, Component title) {
        super(ui, inventory, title);
    }

    private int x;
    private int y;

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float delta) {
        x = (width - imageWidth) / 2;
        y = (height - imageHeight) / 2;

        renderBackground(graphics);
        super.render(graphics, mx, my, delta);
        graphics.drawString(font, Power.from(ui.data, 1) + " / " + MAX_POWER + " 10(E/T)", x, y + 16, 0xFFFFFFFF);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mx, int my) {
        RenderSystem.enableBlend();
        graphics.blit(TEXTURE, x + 57, y + 37, 176, 0, 5 * ui.data.get(0) /* Progress */, 14);
        RenderSystem.disableBlend();
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        super.renderBackground(graphics);
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }
}
