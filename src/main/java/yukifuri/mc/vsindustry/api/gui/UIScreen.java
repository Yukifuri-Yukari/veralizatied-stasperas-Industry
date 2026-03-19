package yukifuri.mc.vsindustry.api.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

/**
 * Client-only base screen class that pairs with a {@link UI} menu.
 * <p>
 * Each concrete subclass renders the background and widgets for one specific menu type.
 * Override {@link #renderBg} to draw the GUI texture and dynamic elements.
 * <p>
 * 与 {@link UI} 菜单配对的纯客户端基础屏幕类.
 * <p>
 * 每个具体子类负责渲染一种特定菜单类型的背景与控件.
 * 覆写 {@link #renderBg} 以绘制 GUI 纹理及动态元素.
 */
public abstract class UIScreen<T extends UI<T>> extends AbstractContainerScreen<T> implements MenuAccess<T> {
    protected final T ui;
    protected final Inventory inventory;

    public UIScreen(T ui, Inventory inventory, Component invTitle) {
        super(ui, inventory, invTitle);
        this.ui = ui;
        this.inventory = inventory;
    }

    /**
     * Renders the full screen, including background, slots, and tooltips.
     * <p>
     * 渲染整个屏幕, 包括背景, 槽位及工具提示.
     */
    @Override
    public void render(GuiGraphics graphics, int mx, int my, float delta) {
        super.render(graphics, mx, my, delta);
    }

    /**
     * Override to render the GUI background texture and any dynamic visuals (e.g. progress bars).
     * <p>
     * 覆写以渲染 GUI 背景纹理及任何动态视觉元素(如进度条).
     */
    @Override
    protected void renderBg(GuiGraphics graphics, float delta, int mx, int my) { }

    /**
     * Returns the paired {@link UI} menu instance.
     * <p>
     * 返回配对的 {@link UI} 菜单实例.
     */
    @Override @NotNull
    public T getMenu() {
        return ui;
    }
}
