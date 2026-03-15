package yukifuri.mc.vsindustry.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import yukifuri.mc.vsindustry.gui.api.UI;
import yukifuri.mc.vsindustry.gui.api.UIScreen;
import yukifuri.mc.vsindustry.gui.ui.CompressorUi;

public class CompressorScreen extends UIScreen<CompressorUi> {
    public static final ResourceLocation TEXTURE =
            new ResourceLocation("vs_industry:textures/gui/compressor.png");

    public CompressorScreen(CompressorUi ui, Inventory inventory, Component title) {
        super(ui, inventory, title);
    }

    public CompressorScreen(UI<CompressorUi> ui, Inventory inventory, Component title) {
        this((CompressorUi) ui, inventory, title);
    }

    private int x;
    private int y;

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float delta) {
        x = (width - imageWidth) / 2;
        y = (height - imageHeight) / 2;

        renderBackground(graphics);
        super.render(graphics, mx, my, delta);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mx, int my) {
        RenderSystem.enableBlend();
        graphics.blit(TEXTURE, x + 57, y + 37, 176, 0, 5 * ui.progress.get(0), 14);
        RenderSystem.disableBlend();
    }

    @Override
    public void renderBackground(GuiGraphics graphics) {
        super.renderBackground(graphics);
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);
    }
}
