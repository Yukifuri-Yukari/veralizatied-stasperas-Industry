package yukifuri.mc.vsindustry.gui.ui;

import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import yukifuri.mc.vsindustry.gui.api.UI;
import yukifuri.mc.vsindustry.gui.api.UIType;
import net.minecraft.world.inventory.Slot;
import yukifuri.mc.vsindustry.network.NetworkProcessor;
import yukifuri.mc.vsindustry.network.packets.SyncGuiDataPacket;

public class CompressorUi extends UI<CompressorUi> {
    public static final Component TITLE = Component.translatable("block.vs_industry.compressor");
    public static final UIType<CompressorUi> TYPE = UIType.of(CompressorUi::createUI);

    public final Container container;

    private ItemStack originalInput;
    private ItemStack originalOutput;

    public final Slot input;
    public final Slot output;

    public final ContainerData data;

    public CompressorUi(int syncId, Inventory inventory, Container c, ContainerData dataC) {
        super(TYPE, TITLE, inventory, syncId);
        container = c;
        input = new Slot(container, 0, 33, 37);
        output = new ResultSlot(inv.player, container, 1, 112, 37);
        data = dataC;
        init();
    }

    public CompressorUi(int syncId, Inventory inventory) {
        this(syncId, inventory, new SimpleContainer(2), new SimpleContainerData(3));
    }

    @Override
    public void init() {
        addInventorySlots(inv);
        addSlots(input, output);
        originalInput = input.getItem();
        originalOutput = output.getItem();
    }

    @Override
    public void broadcastChanges() {
        super.broadcastChanges();
        if (!ItemStack.matches(originalInput, input.getItem()) || !ItemStack.matches(originalOutput, output.getItem())) {
            originalInput = input.getItem();
            originalOutput = output.getItem();
            NetworkProcessor.getInstance().sendToClient(
                    new SyncGuiDataPacket(containerId, (buf) -> buf
                            .writeItem(input.getItem())
                            .writeItem(output.getItem())
                    ), inv.player
            );
        }
    }

    @Override
    public void syncDataOnClient(SyncGuiDataPacket packet) {
        var buf = packet.getPayload();
        input.set(buf.readItem());
        output.set(buf.readItem());
    }

    public static CompressorUi createUI(int syncId, Inventory inventory) {
        return new CompressorUi(syncId, inventory);
    }

    @Override @NotNull
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack itemStack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack itemStack2 = slot.getItem();
            itemStack = itemStack2.copy();

            if (index < inventorySize()) {
                // Inv -> Input
                if (!moveItemStackTo(itemStack2, inventorySize(), inventorySize() + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == inventorySize()) {
                // Input -> Inv
                if (!moveItemStackTo(itemStack2, 0, inventorySize(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index == inventorySize() + 1) {
                // Output -> Inv
                if (!moveItemStackTo(itemStack2, 0, inventorySize(), true)) {
                    return ItemStack.EMPTY;
                }
            }

            if (itemStack2.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return itemStack;
    }

    static class ResultSlot extends Slot {
        private int removeCount;

        public ResultSlot(Player player, Container container, int i, int j, int k) {
            super(container, i, j, k);
        }

        @Override
        public boolean mayPlace(ItemStack itemStack) {
            return false;
        }

        @Override @NotNull
        public ItemStack remove(int i) {
            if (this.hasItem()) {
                this.removeCount = this.removeCount + Math.min(i, this.getItem().getCount());
            }

            return super.remove(i);
        }

        @Override
        public void onTake(Player player, ItemStack itemStack) {
            this.checkTakeAchievements(itemStack);
            super.onTake(player, itemStack);
        }

        @Override
        protected void onQuickCraft(ItemStack itemStack, int i) {
            this.removeCount += i;
            this.checkTakeAchievements(itemStack);
        }
    }
}