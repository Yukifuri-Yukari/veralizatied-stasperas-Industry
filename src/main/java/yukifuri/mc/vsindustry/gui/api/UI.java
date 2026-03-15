package yukifuri.mc.vsindustry.gui.api;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import yukifuri.mc.vsindustry.gui.api.widget.Widget;
import yukifuri.mc.vsindustry.network.NetworkProcessor;
import yukifuri.mc.vsindustry.network.packets.SyncGuiDataPacket;
import yukifuri.mc.vsindustry.util.WorkInProgress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static yukifuri.mc.vsindustry.util.Math.doubleHash;

/**
 * An abstract class used to create an in-game Menu. <p>
 * In-game GUIs are used to interact with level objects. <p>
 * <p>
 * Different from {@link net.minecraft.client.gui.screens.Screen}, <p>
 * Menus are 2 sides sided, like {@link net.minecraft.world.inventory.AbstractContainerMenu}, <p>
 * but Screens are used like {@link net.minecraft.client.gui.screens.TitleScreen}. <p>
 *
 * @author 雪降り
 */
@MethodsReturnNonnullByDefault
public abstract class UI<T extends UI<T>> extends AbstractContainerMenu {
    protected final Component title;
    protected final UIType<T> type;
    protected final Inventory inv;
    protected final List<Widget> widgets = new ArrayList<>();

    protected boolean hasInventorySlots = false;

    protected final ClientActionHandler handler = new ClientActionHandler();

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
     */
    public void init() { }

    /**
     * Adds inventory slots to the menu.<p>
     * Automatically adds the player's inventory.
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
     * If set hasInventorySlots is true, returns 36 (4 * 9, 3 * 9 inventory + 9 hotbar),
     * else returns 0
     */
    protected final int inventorySize() {
        return hasInventorySlots ? 36 : 0;
    }

    /**
     * Server side method.
     * Calls once a tick.
     * <p>
     * Make sure its performance is good.
     */
    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
    }

    protected void addWidget(Widget widget) {
        widgets.add(widget);
    }

    /**
     * Client-side method.
     * <p>
     * Called when the server sends data to the client.
     * Caller: {@link NetworkProcessor.ClientProcessor}
     */
    public void syncDataOnClient(SyncGuiDataPacket packet) { }

    protected final void addSlots(Slot... slots) {
        for (Slot slot : slots) {
            addSlot(slot);
        }
    }

    /**
     * 2 side method.
     * <p>
     * Returns the menu type.
     */
    @Override
    public final MenuType<T> getType() {
        return type.get();
    }

    /**
     * 2 side method.
     * <p>
     * Returns true if the menu is still valid.
     * Or minecraft will force to close the menu.
     */
    @Override
    public boolean stillValid(Player player) {
        return true;
    }

    /**
     * Handles the transfer of items from the player's inventory to the menu.
     * quickMove: Shift+RMouse to move items.
     */
    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return null;
    }

    public ClientActionHandler getHandler() {
        return handler;
    }

    public class ClientActionHandler {
        private final Map<Long, CAHandler> HANDLERS = new HashMap<>();

        public void register(String name, CAHandler handler) {
            var hash = doubleHash(name);
            if (HANDLERS.containsKey(hash))
                throw new IllegalArgumentException("Handler already registered for " + name);
            HANDLERS.put(hash, handler);
        }

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

        public void receiveClientAction(SyncGuiDataPacket packet) {
            var payload = packet.getPayload();
            var hash = payload.readLong();
            var handler = HANDLERS.get(hash);
            if (handler == null)
                throw new IllegalArgumentException("No handler found for " + hash);
            handler.handle(payload);
        }
    }

    @FunctionalInterface
    public interface CAHandler {
        void handle(FriendlyByteBuf args);
    }
}