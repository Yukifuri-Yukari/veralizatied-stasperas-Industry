package yukifuri.mc.vsindustry.gui.api;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Each UIScreen pairs with a UI.
 */
public abstract class UIScreen<T extends UI<T>> extends AbstractContainerScreen<T> implements MenuAccess<T> {
    protected final T ui;
    protected final Inventory inventory;

    public UIScreen(T ui, Inventory inventory, Component invTitle) {
        super(ui, inventory, invTitle);
        this.ui = ui;
        this.inventory = inventory;
    }

    @Override
    public void render(GuiGraphics graphics, int mx, int my, float delta) {
        super.render(graphics, mx, my, delta);
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mx, int my) { }

    @Override @NotNull
    public T getMenu() {
        return ui;
    }
}
