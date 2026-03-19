package yukifuri.mc.vsindustry.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import yukifuri.mc.vsindustry.logic.recipe.CompressorRecipe;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import yukifuri.mc.vsindustry.api.level.block.Connectable;
import yukifuri.mc.vsindustry.api.level.blockentity.BaseContainerBlockEntity;
import yukifuri.mc.vsindustry.api.level.blockentity.DefaultNode;
import yukifuri.mc.vsindustry.api.level.blockentity.SimpleBlockWithEntity;
import yukifuri.mc.vsindustry.api.level.container.ProvidedWorldlyContainer;
import yukifuri.mc.vsindustry.ui.CompressorUi;
import yukifuri.mc.vsindustry.logic.level.node.GridNode;
import yukifuri.mc.vsindustry.registries.VBlocks;
import yukifuri.mc.vsindustry.util.Power;

import static yukifuri.mc.vsindustry.api.gui.UI.SLOTS_FOR_NOTHING;

@MethodsReturnNonnullByDefault
public class Compressor extends SimpleBlockWithEntity<Compressor.Entity> implements WorldlyContainerHolder, Connectable {
    public static final String
            NBT_PROGRESS = "Progress",
            NBT_POWER_STORGE = "PowerStorge";

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

    public Compressor() {
        super(BlockBehaviour.Properties
                .copy(Blocks.IRON_BLOCK)
        );
        registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
        return defaultBlockState()
                .setValue(FACING, blockPlaceContext
                        .getHorizontalDirection()
                        .getOpposite()
                );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
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
    public void setPlacedBy(
            Level level, BlockPos pos,
            BlockState state, LivingEntity placer,
            ItemStack stack
    ) {
        if (stack.hasCustomHoverName()) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof Compressor.Entity entity) {
                entity.setCustomName(stack.getHoverName());
            }
        }
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos blockPos, BlockState blockState) {
        return new Entity(blockPos, blockState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState blockState,
            BlockEntityType<T> blockEntityType
    ) {
        return level.isClientSide() ? null :
                (level1, pos, state, entity) -> tick(level1, pos, state, (Entity) entity);
    }

    @Override
    public InteractionResult use(
            BlockState state, Level level,
            BlockPos pos, Player player,
            InteractionHand hand, BlockHitResult result
    ) {
        if (level.isClientSide()) return InteractionResult.PASS;
        player.openMenu((Entity) level.getBlockEntity(pos));

        return InteractionResult.SUCCESS;
    }

    @Override
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
        if (be instanceof Compressor.Entity entity) {
            Containers.dropContents(level, pos, entity);
        }
        super.onRemove(oldState, level, pos, newState, movedByPiston);
    }

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, Compressor.Entity entity) {
        var container = entity.container;
        var input = container.getItem(0);
        var output = container.getItem(1);

        var recipe = level.getRecipeManager()
                .getRecipeFor(CompressorRecipe.TYPE, entity.container, level)
                .orElse(null);

        if (recipe == null) {
            if (entity.getProgress() > 0) entity.setProgress(entity.getProgress() - 1);
            return;
        }

        entity.setRecipeDuration(recipe.getDuration());

        // Check output capacity
        var result = recipe.getResultItem(level.registryAccess());
        if (!output.isEmpty()) {
            if (!ItemStack.isSameItemSameTags(output, result)) return;
            if (output.getCount() + result.getCount() > output.getMaxStackSize()) return;
        }

        if (entity.getPowerStorge() < Entity.POWER_PER_TICK) {
            if (entity.getProgress() > 0) entity.setProgress(entity.getProgress() - 1);
            return;
        }

        if (recipe != entity.lastRecipe) {
            entity.setProgress(0);
            entity.lastRecipe = recipe;
        }

        entity.setPowerStorge(entity.getPowerStorge() - Entity.POWER_PER_TICK);
        entity.setProgress(entity.getProgress() + 1);

        if (entity.getProgress() >= recipe.getDuration()) {
            entity.setProgress(0);
            input.shrink(recipe.getConsumes());
            if (output.isEmpty()) {
                container.setItem(1, result.copy());
            } else {
                output.grow(result.getCount());
            }
        }
    }

    @Override
    public RenderShape getRenderShape(BlockState blockState) {
        return RenderShape.MODEL;
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter getter, BlockPos pos, BlockState state) {
        var is = super.getCloneItemStack(getter, pos, state);
        var entity = getter.getBlockEntity(pos, Compressor.Entity.TYPE);
        entity.ifPresent(ent -> ent.saveToItem(is));

        return is;
    }

    @Override
    public WorldlyContainer getContainer(BlockState state, LevelAccessor level, BlockPos pos) {
        return (Compressor.Entity) level.getBlockEntity(pos);
    }

    public static class Entity extends BaseContainerBlockEntity implements ProvidedWorldlyContainer, DefaultNode {
        public static final BlockEntityType<Entity> TYPE = FabricBlockEntityTypeBuilder
                .create(Entity::new, VBlocks.COMPRESSOR)
                .build();

        public static final int[]
                SLOTS_FOR_UP = {0},
                SLOTS_FOR_DOWN = {1};

        public static final long MAX_POWER = 2000;
        public static final long POWER_PER_TICK = 5;

        public final SimpleContainer container = new SimpleContainer(2);

        // slots: 0=progress, 1=powerStorge high, 2=powerStorge low, 3=recipeDuration
        public final ContainerData syncData = new SimpleContainerData(4);

        public CompressorRecipe lastRecipe = null;

        public Entity(BlockPos pos, BlockState state) {
            this(pos, state, 0, 0L);
        }

        public Entity(BlockPos pos, BlockState state, int progress, long powerStorge) {
            super(TYPE, pos, state);
            setProgress(progress);
            setPowerStorge(powerStorge);
        }

        //region Power
        @Override
        public long expectedPower() {
            if (getPowerStorge() >= MAX_POWER) return 0;
            return POWER_PER_TICK;
        }

        @Override
        public void powerAccepted(long power) {
            setPowerStorge(Math.min(MAX_POWER, getPowerStorge() + power));
        }
        //endregion

        //region NBT
        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);
            if (getProgress() > 0) tag.putInt(NBT_PROGRESS, getProgress());
            if (getPowerStorge() > 0) tag.putLong(NBT_POWER_STORGE, getPowerStorge());
            var input = container.getItem(0);
            var output = container.getItem(1);
            if (!input.isEmpty()) tag.put("Input", input.save(new CompoundTag()));
            if (!output.isEmpty()) tag.put("Output", output.save(new CompoundTag()));
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            setProgress(tag.contains(NBT_PROGRESS) ? tag.getInt(NBT_PROGRESS) : 0);
            setPowerStorge(tag.contains(NBT_POWER_STORGE) ? tag.getLong(NBT_POWER_STORGE) : 0L);
            container.setItem(0, tag.contains("Input") ? ItemStack.of(tag.getCompound("Input")) : ItemStack.EMPTY);
            container.setItem(1, tag.contains("Output") ? ItemStack.of(tag.getCompound("Output")) : ItemStack.EMPTY);
        }
        //endregion

        //region Container
        @Override
        protected Component getDefaultName() {
            return CompressorUi.TITLE;
        }

        @Override
        protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
            return new CompressorUi(i, inventory, this, syncData);
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
            return switch (side) {
                case UP -> SLOTS_FOR_UP;
                case DOWN -> SLOTS_FOR_DOWN;
                default -> SLOTS_FOR_NOTHING;
            };
        }

        @Override
        public boolean canPlaceItemThroughFace(int index, ItemStack itemStack, @Nullable Direction direction) {
            return index == 0;
        }

        @Override
        public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction direction) {
            return direction == Direction.DOWN && index == 1;
        }

        public int getProgress() {
            return syncData.get(0);
        }

        public void setProgress(int progress) {
            syncData.set(0, progress);
        }

        public long getPowerStorge() {
            return Power.from(syncData, 1);
        }

        public void setPowerStorge(long powerStorge) {
            Power.to(syncData, powerStorge, 1);
        }

        public void setRecipeDuration(int duration) {
            syncData.set(3, duration);
        }
        //endregion

        //region Grid
        private GridNode gridNode;

        @Override
        public GridNode getGridNode() {
            if (gridNode == null) gridNode = GridNode.of(this);
            return gridNode;
        }

        protected void onFirstTick(ServerLevel level) {
            defaultOnFirstTick(level);
        }

        protected void onRemoved(ServerLevel level) {
            defaultOnRemoved(level);
        }

        public void onChunkUnload() {
            defaultOnChunkUnload();
        }
        //endregion
    }
}
