package yukifuri.mc.vsindustry.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import org.jetbrains.annotations.Nullable;
import yukifuri.mc.vsindustry.api.level.block.Connectable;
import yukifuri.mc.vsindustry.api.level.blockentity.BaseContainerBlockEntity;
import yukifuri.mc.vsindustry.api.level.blockentity.SimpleBlockWithEntity;
import yukifuri.mc.vsindustry.api.level.container.ProvidedContainer;
import yukifuri.mc.vsindustry.logic.level.network.power.DefaultPowerNetworkNode;
import yukifuri.mc.vsindustry.logic.level.network.power.PowerNetworkNode;
import yukifuri.mc.vsindustry.registries.VBlocks;

import net.minecraft.world.entity.player.Player;

@MethodsReturnNonnullByDefault
public class MineExcavator extends SimpleBlockWithEntity<MineExcavator.Entity> implements Connectable {
    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public MineExcavator() {
        super(BlockBehaviour.Properties.copy(Blocks.IRON_BLOCK));
        registerDefaultState(stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(FORMED, false));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext ctx) {
        return defaultBlockState()
                .setValue(FACING, ctx.getHorizontalDirection().getOpposite())
                .setValue(FORMED, false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder
                .add(FACING)
                .add(FORMED);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
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
        if (be instanceof MineExcavator.Entity entity) {
            Containers.dropContents(level, pos, entity);
        }
        super.onRemove(oldState, level, pos, newState, movedByPiston);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Entity(pos, state);
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, Entity blockEntity) {

    }

    public static class Entity extends BaseContainerBlockEntity implements DefaultPowerNetworkNode, ProvidedContainer {
        public static final BlockEntityType<Entity> TYPE = FabricBlockEntityTypeBuilder
                .create(Entity::new, VBlocks.MINE_EXCAVATOR)
                .build();
        public static final Component TITLE = Component.literal("");

        private final SimpleContainer container = new SimpleContainer(1);
        private final ContainerData data = new SimpleContainerData(3); // 0 - progress, 1-2 - energy

        protected Entity(BlockPos pos, BlockState blockState) {
            super(TYPE, pos, blockState);
        }

        @Override
        protected Component getDefaultName() {
            return TITLE;
        }

        @Override
        protected AbstractContainerMenu createMenu(int containerId, Inventory inventory) {
            throw new UnsupportedOperationException();
            // No need to create a menu for this block.
        }

        @Override
        public Container getContainer() {
            return container;
        }

        @Override
        public boolean stillValid(Player player) {
            return false;
        }

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