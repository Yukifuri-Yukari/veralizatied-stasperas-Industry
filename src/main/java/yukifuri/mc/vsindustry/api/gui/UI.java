package yukifuri.mc.vsindustry.api.gui;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import yukifuri.mc.vsindustry.api.gui.widget.Widget;
import yukifuri.mc.vsindustry.logic.network.NetworkProcessor;
import yukifuri.mc.vsindustry.logic.network.packets.SyncGuiDataPacket;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static yukifuri.mc.vsindustry.util.Math.doubleHash;

/**
 * An abstract class used to create an in-game Menu.
 * <p>
 * In-game GUIs are used to interact with level objects.
 * Different from {@link net.minecraft.client.gui.screens.Screen},
 * Menus are 2-sided like {@link net.minecraft.world.inventory.AbstractContainerMenu},
 * but Screens are client-only like {@link net.minecraft.client.gui.screens.TitleScreen}.
 * <p>
 * 用于创建游戏内菜单的抽象类.
 * <p>
 * 游戏内 GUI 用于与世界对象进行交互.
 * 与 {@link net.minecraft.client.gui.screens.Screen} 不同,
 * Menu 是双端的(类似 {@link net.minecraft.world.inventory.AbstractContainerMenu}),
 * 而 Screen 仅在客户端存在(类似 {@link net.minecraft.client.gui.screens.TitleScreen}).
 *
 * @author 雪降り
 */
@MethodsReturnNonnullByDefault
public abstract class UI<T extends UI<T>> extends AbstractContainerMenu {
    /** Empty slot array, used when a sided container exposes no slots in a direction. <p> 空槽位数组, 用于方向性容器在某方向不暴露槽位时. */
    public static final int[] SLOTS_FOR_NOTHING = new int[0];

    protected final Component title;
    protected final UIType<T> type;
    protected final Inventory inv;
    protected final List<Widget> widgets = new ArrayList<>();

    protected boolean hasInventorySlots = false;

    protected final ClientActionHandler handler = new ClientActionHandler();

    /**
     * Constructs the menu with the given type, title, player inventory, and sync ID.
     * <p>
     * 使用给定类型, 标题, 玩家背包及同步 ID 构造菜单.
     */
    public UI(UIType<T> type, Component title, Inventory inventory, int id) {
        super(type.get(), id);
        this.title = title;
        this.type = type;
        this.inv = inventory;
    }

    /**
     * Called when the menu is constructed.
     * <p>
     * Call it by your constructor.
     * <p>
     * 菜单构造时调用. 请在子类构造函数中手动调用.
     */
    public void init() { }

    /**
     * Adds inventory slots to the menu.
     * Automatically adds the player's inventory (27 storage + 9 hotbar = 36 slots).
     * <p>
     * 向菜单添加玩家背包槽位(27 格存储 + 9 格快捷栏 = 36 格).
     */
    protected final void addInventorySlots(Inventory inventory) {
        for (int j = 0; j < 3; j++) {
            for (int k = 0; k < 9; k++) {
                this.addSlot(new Slot(inventory, k + j * 9 + 9, 8 + k * 18, 84 + j * 18));
            }
        }

        for (int j = 0; j < 9; j++) {
            this.addSlot(new Slot(inventory, j, 8 + j * 18, 142));
        }
        hasInventorySlots = true;
    }

    /**
     * If {@code hasInventorySlots} is true, returns 36 (3*9 inventory + 9 hotbar),
     * otherwise returns 0.
     * <p>
     * 若 {@code hasInventorySlots} 为 true 则返回 36(3*9 背包 + 9 快捷栏), 否则返回 0.
     */
    protected final int inventorySize() {
        return hasInventorySlots ? 36 : 0;
    }

    /**
     * Server-side method. Called once a tick to sync container data to watching clients.
     * <p>
     * Make sure its performance is good.
     * <p>
     * 服务端方法, 每 tick 调用一次, 用于将容器数据同步至正在观看的客户端.
     * <p>
     * 请确保其性能表现良好.
     */
    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
    }

    /**
     * Registers a widget to be rendered by the paired {@link UIScreen}.
     * <p>
     * 注册一个由配对 {@link UIScreen} 负责渲染的控件.
     */
    protected void addWidget(Widget widget) {
        widgets.add(widget);
    }

    /**
     * Client-side method. Called when the server sends a {@link SyncGuiDataPacket} to the client.
     * <p>
     * Override to read custom data from the packet payload and update client-side state.
     * Caller: {@link NetworkProcessor.ClientProcessor}
     * <p>
     * 客户端方法. 服务端向客户端发送 {@link SyncGuiDataPacket} 时调用.
     * <p>
     * 覆写以从数据包载荷中读取自定义数据并更新客户端状态.
     */
    public void syncDataOnClient(SyncGuiDataPacket packet) { }

    /**
     * Convenience method to add multiple slots at once.
     * <p>
     * 便捷方法, 一次性添加多个槽位.
     */
    protected final void addSlots(Slot... slots) {
        for (Slot slot : slots) {
            addSlot(slot);
        }
    }

    /**
     * 2-sided method. Returns the menu type.
     * <p>
     * 双端方法. 返回菜单类型.
     */
    @Override
    public final MenuType<T> getType() {
        return type.get();
    }

    /**
     * 2-sided method. Returns {@code true} if the menu is still valid;
     * Minecraft will force-close the menu if this returns {@code false}.
     * <p>
     * 双端方法. 返回 {@code true} 表示菜单仍然有效;
     * 若返回 {@code false}, Minecraft 将强制关闭菜单.
     */
    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * Handles Shift+Click item transfer between the player inventory and the menu slots.
     * Returns the original stack before the move, or {@link ItemStack#EMPTY} if nothing moved.
     * <p>
     * 处理玩家背包与菜单槽位间的 Shift+点击物品转移.
     * 返回移动前的原始物品堆叠, 若未移动任何物品则返回 {@link ItemStack#EMPTY}.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    /**
     * Returns the {@link ClientActionHandler} for dispatching named client→server actions.
     * <p>
     * 返回用于分发命名客户端→服务端动作的 {@link ClientActionHandler}.
     */
    public ClientActionHandler getHandler() {
        return handler;
    }

    /**
     * Dispatcher for named client→server actions within a menu.
     * <p>
     * Each action is identified by a {@code doubleHash} of its name string to avoid string
     * transmission overhead. Handlers are registered on the server side and invoked when
     * the client sends a matching action packet.
     * <p>
     * 菜单内命名客户端→服务端动作的分发器.
     * <p>
     * 每个动作通过其名称字符串的 {@code doubleHash} 标识, 以避免传输字符串的开销.
     * 处理器在服务端注册, 当客户端发送匹配的动作数据包时被调用.
     */
    public class ClientActionHandler {
        private final Map<Long, CAHandler> HANDLERS = new HashMap<>();

        /**
         * Registers a handler for the given action name.
         * Throws if a handler with the same name hash already exists.
         * <p>
         * 为给定动作名称注册处理器.
         * 若相同名称哈希的处理器已存在则抛出异常.
         */
        public void register(String name, CAHandler handler) {
            var hash = doubleHash(name);
            if (HANDLERS.containsKey(hash))
                throw new IllegalArgumentException("Handler already registered for " + name);
            HANDLERS.put(hash, handler);
        }

        /**
         * Sends an action packet from the client to the server.
         * The packet carries the action hash followed by the provided argument bytes.
         * <p>
         * 从客户端向服务端发送动作数据包.
         * 数据包包含动作哈希值及后续的参数字节.
         */
        public void sendToServer(String name, FriendlyByteBuf args) {
            var hash = doubleHash(name);
            if (!HANDLERS.containsKey(hash))
                throw new IllegalArgumentException("No handler found for " + name);
            NetworkProcessor.getInstance().sendToServer(
                    new SyncGuiDataPacket(containerId, buf -> {
                        buf.writeLong(hash);
                        buf.writeBytes(args);
                    })
            );
        }

        /**
         * Called on the server side to dispatch an incoming client action packet
         * to the registered {@link CAHandler}.
         * <p>
         * 在服务端调用, 将收到的客户端动作数据包分发给已注册的 {@link CAHandler}.
         */
        public void receiveClientAction(SyncGuiDataPacket packet) {
            var payload = packet.getPayload();
            var hash = payload.readLong();
            var handler = HANDLERS.get(hash);
            if (handler == null)
                throw new IllegalArgumentException("No handler found for " + hash);
            handler.handle(payload);
        }
    }

    /**
     * Functional interface for handling a client→server action with a byte-buffer payload.
     * <p>
     * 处理携带字节缓冲载荷的客户端→服务端动作的函数式接口.
     */
    @FunctionalInterface
    public interface CAHandler {
        void handle(FriendlyByteBuf args);
    }
}