package yukifuri.mc.vsindustry.block.energy.generator;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import yukifuri.mc.vsindustry.api.level.block.Connectable;
import yukifuri.mc.vsindustry.api.level.blockentity.BaseContainerBlockEntity;
import yukifuri.mc.vsindustry.api.level.blockentity.*;
import yukifuri.mc.vsindustry.api.level.container.ProvidedWorldlyContainer;
import yukifuri.mc.vsindustry.logic.level.network.power.DefaultPowerNetworkNode;
import yukifuri.mc.vsindustry.logic.level.network.power.PowerNetworkNode;
import yukifuri.mc.vsindustry.ui.energy.generator.ThermoelectricUi;
import yukifuri.mc.vsindustry.registries.VBlocks;
import yukifuri.mc.vsindustry.tags.ThermoelectricFuelTags;
import yukifuri.mc.vsindustry.util.Power;

import static yukifuri.mc.vsindustry.api.gui.UI.SLOTS_FOR_NOTHING;

@MethodsReturnNonnullByDefault
public class ThermoelectricGenerator extends SimpleBlockWithEntity<ThermoelectricGenerator.Entity> implements WorldlyContainerHolder, Connectable {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public ThermoelectricGenerator() {
        super(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK));
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    public WorldlyContainer getContainer(BlockState state, LevelAccessor level, BlockPos pos) {
        return (Entity) level.getBlockEntity(pos);
    }

    @Override
    public InteractionResult use(
            BlockState state, Level level, BlockPos pos,
            Player player, InteractionHand hand, BlockHitResult hit
    ) {
        if (level.isClientSide) return InteractionResult.PASS;
        player.openMenu((Entity) level.getBlockEntity(pos));

        return InteractionResult.SUCCESS;
    }

    public void onRemove(
            BlockState oldState, Level level,
            BlockPos pos, BlockState newState,
            boolean movedByPiston
    ) {
        if (level.isClientSide()) {
            super.onRemove(oldState, level, pos, newState, movedByPiston);
            return;
        }
        if (oldState.is(newState.getBlock())) {
            super.onRemove(oldState, level, pos, newState, movedByPiston);
            return;
        }
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof ThermoelectricGenerator.Entity entity) {
            Containers.dropContents(level, pos, entity);
        }
        super.onRemove(oldState, level, pos, newState, movedByPiston);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (entity.getPower() == Entity.MAX_POWER) return;

        // Convert progress → power: 60 progress = 100 power
        if (entity.getProgress() >= 60) {
            if (entity.getPower() < Entity.MAX_POWER) {
                entity.setProgress(entity.getProgress() - 60);
                entity.setPower(Math.min(Entity.MAX_POWER, entity.getPower() + 100));
                return;
            }
        }

        var is = entity.getItem(0);

        int burnTime;

        if (is.is(ThermoelectricFuelTags.TAG)) {
            burnTime = 400; // 20s
        } else {
            burnTime = AbstractFurnaceBlockEntity.getFuel().getOrDefault(is.getItem(), 0);
        }

        if (burnTime == 0) return; // Do not put unburnable items!

        is.shrink(1);
        int gain = burnTime / 10;
        if (entity.getPower() < Entity.MAX_POWER) {
            gain = gain * 2 / 3;
            entity.setPower(Math.min(Entity.MAX_POWER, entity.getPower() + 20));
        }

        entity.setProgress(entity.getProgress() + gain);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type
    ) {
        return level.isClientSide ? null :
                (level1, pos, state1, entity) -> tick(level1, pos, state1, (Entity) entity);
    }

    public static class Entity extends BaseContainerBlockEntity implements ProvidedWorldlyContainer, DefaultPowerNetworkNode {
        public static final BlockEntityType<Entity> TYPE = FabricBlockEntityTypeBuilder
                .create(Entity::new, VBlocks.THERMOELECTRIC_GENERATOR)
                .build();

        public static final int[]
                SLOTS_FOR_UP = new int[]{0};

        public static final int MAX_PROGRESS = 1024;
        public static final int MAX_POWER = 10000;

        public final SimpleContainer container = new SimpleContainer(1);
        public final ContainerData data = new SimpleContainerData(3);

        public Entity(BlockPos pos, BlockState state) {
            this(pos, state, 0, 0L);
        }

        public Entity(BlockPos pos, BlockState state, int progress, long power) {
            super(TYPE, pos, state);
            setProgress(progress);
            setPower(power);
        }

        //region NBT
        private static final String NBT_PROGRESS = "Progress";
        private static final String NBT_POWER = "Power";
        private static final String NBT_FUEL = "Fuel";

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            if (getProgress() > 0) tag.putInt(NBT_PROGRESS, getProgress());
            if (getPower() > 0) tag.putLong(NBT_POWER, getPower());
            var fuel = container.getItem(0);
            if (!fuel.isEmpty()) tag.put(NBT_FUEL, fuel.save(new CompoundTag()));
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            setProgress(tag.contains(NBT_PROGRESS) ? tag.getInt(NBT_PROGRESS) : 0);
            setPower(tag.contains(NBT_POWER) ? tag.getLong(NBT_POWER) : 0L);
            container.setItem(0, tag.contains(NBT_FUEL) ? ItemStack.of(tag.getCompound(NBT_FUEL)) : ItemStack.EMPTY);
        }
        //endregion

        //region Power
        @Override
        public long expectedPower() {
            return 0;
        }

        @Override
        public long powerSuppliable() {
            var power = getPower();
            return power >= 1000 ? 1000 : power;
        }

        @Override
        public void powerConsumed(long amount) {
            setPower(getPower() - amount);
        }
        //endregion

        //region Container
        @Override
        protected Component getDefaultName() {
            return ThermoelectricUi.TITLE;
        }

        @Override
        protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
            return new ThermoelectricUi(containerId, inventory, container, data);
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }

        @Override
        public Container getContainer() {
            return container;
        }

        @Override
        public int[] getSlotsForFace(Direction side) {
            return side == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_NOTHING;
        }

        @Override
        public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
            return index == 0;
        }

        @Override
        public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
            return direction == Direction.DOWN && index == 0 && stack.is(Items.BUCKET);
        }

        public int getProgress() {
            return data.get(0);
        }

        public void setProgress(int progress) {
            data.set(0, progress);
        }

        public long getPower() {
            return Power.from(data, 1);
        }

        public void setPower(long power) {
            Power.to(data, power, 1);
        }
        //endregion

        //region Grid
        private PowerNetworkNode node;
        @Override
        public PowerNetworkNode getNode() {
            if (node == null) node = PowerNetworkNode.of(this);
            return node;
        }

        protected void onFirstTick(ServerLevel level) {
            defaultOnFirstTick(level);
        }

        protected void onRemoved(ServerLevel level) {
            defaultOnRemoved(level);
        }

        public void onChunkUnload() {
            if (level instanceof ServerLevel sl) defaultOnChunkUnload(sl);
        }
        //endregion
    }
}
