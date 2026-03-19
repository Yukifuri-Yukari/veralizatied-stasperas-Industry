package yukifuri.mc.vsindustry.api.gui.widget;

import net.minecraft.client.gui.GuiGraphics;
import yukifuri.mc.vsindustry.api.gui.UI;
import yukifuri.mc.vsindustry.api.gui.UIScreen;

/**
 * Abstract base class for renderable GUI widgets attached to a {@link UI}.
 * <p>
 * Widgets are registered via {@link UI#addWidget(Widget)}
 * and rendered by the paired {@link UIScreen}.
 * <p>
 * 附加到 {@link UI} 的可渲染 GUI 控件抽象基类.
 * <p>
 * 控件通过 {@link UI#addWidget(Widget)} 注册,
 * 并由配对的 {@link UIScreen} 负责渲染.
 */
public abstract class Widget {
    /**
     * Renders this widget each frame.
     * <p>
     * 每帧渲染此控件.
     *
     * @param graphics    the graphics context / 图形上下文
     * @param mouseX      current mouse X position / 当前鼠标 X 坐标
     * @param mouseY      current mouse Y position / 当前鼠标 Y 坐标
     * @param partialTick partial tick for smooth rendering / 用于平滑渲染的部分 tick 值
     */
    public abstract void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick);
}