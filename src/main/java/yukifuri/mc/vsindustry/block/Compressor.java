package yukifuri.mc.vsindustry.block;

import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.*;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;
import yukifuri.mc.vsindustry.api.level.block.Connectable;
import yukifuri.mc.vsindustry.api.level.blockentity.BaseContainerBlockEntity;
import yukifuri.mc.vsindustry.gui.ui.CompressorUi;
import yukifuri.mc.vsindustry.level.node.GridNode;
import yukifuri.mc.vsindustry.level.node.Node;
import yukifuri.mc.vsindustry.registries.VBlocks;
import yukifuri.mc.vsindustry.api.level.container.ProvidedWorldlyContainer;
import yukifuri.mc.vsindustry.api.level.blockentity.SimpleBlockWithEntity;

@MethodsReturnNonnullByDefault
public class Compressor extends SimpleBlockWithEntity<Compressor.Entity> implements WorldlyContainerHolder, Connectable {
    public static final String NBT_PROGRESS = "Progress";
    public static final String NBT_COALS_COUNT = "CoalsCount";
    public static final String NBT_COALS = "Coals";
    public static final String NBT_DIAMONDS_COUNT = "DiamondsCount";
    public static final String NBT_DIAMONDS = "Diamonds";

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

    @Override @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState blockState,
            BlockEntityType<T> blockEntityType
    ) {
        return level.isClientSide() ? null : (level1, pos, state, entity) -> tick(level1, pos, state, (Entity) entity);
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

    private int tickCount = 0;

    @Override
    public void tick(Level level, BlockPos blockPos, BlockState blockState, Compressor.Entity entity) {
        var container = entity.container;

        if (!container.getItem(0).is(Items.COAL)) {
            if (entity.progress.get(0) > 0)
                entity.progress.set(0, entity.progress.get(0) - 1);
            tickCount = 0;
            return;
        }

        boolean enableTransferring = container.getItem(0).is(Items.COAL) && container.getItem(0).getCount() > 0;
        boolean isFull = container.getItem(1).getCount() >= 64;
        boolean canTransfer = entity.progress.get(0) == 10;

        if (enableTransferring && !isFull && canTransfer) {
            entity.progress.set(0, 0);
            if (container.getItem(1) == ItemStack.EMPTY) {
                container.setItem(1, new ItemStack(Items.DIAMOND));
            } else container.getItem(1).grow(1);
            container.getItem(0).shrink(1);
        }

        if (tickCount == 10) {
            tickCount = 0;
            if (enableTransferring && !isFull)
                entity.progress.set(0, entity.progress.get(0) + 1);
        } else
            tickCount++;
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

    public static class Entity extends BaseContainerBlockEntity implements ProvidedWorldlyContainer, Node {
        public static final BlockEntityType<Entity> TYPE = FabricBlockEntityTypeBuilder
                .create(Entity::new, VBlocks.COMPRESSOR)
                .build();

        public static final int[] SLOTS_FOR_UP = new int[] {0};
        public static final int[] SLOTS_FOR_DOWN = new int[] {1};
        public static final int[] SLOTS_FOR_SIDES = new int[] {};

        public final SimpleContainer container = new SimpleContainer(2);
        public final ContainerData progress = new ContainerData() {
            int p;

            @Override
            public int get(int i) {
                return p;
            }

            @Override
            public void set(int i, int j) {
                p = j;
            }

            @Override
            public int getCount() {
                return 1;
            }
        };

        public Entity(BlockPos pos, BlockState state) {
            this(pos, state, 0);
        }

        public Entity(BlockPos pos, BlockState state, int progress) {
            super(TYPE, pos, state);
            this.progress.set(0, progress);
        }

        @Override
        protected void saveAdditional(CompoundTag tag) {
            super.saveAdditional(tag);

            int progressValue = progress.get(0);
            byte coalsCount = (byte) container.getItem(0).getCount();
            byte diamondsCount = (byte) container.getItem(1).getCount();

            if (progressValue == 0 && coalsCount == 0 && diamondsCount == 0) {
                return;
            }

            if (progressValue > 0) {
                tag.putInt(NBT_PROGRESS, progressValue);
            }

            if (coalsCount > 0) {
                tag.putByte(NBT_COALS_COUNT, coalsCount);
                if (container.getItem(0).hasTag()) {
                    tag.put(NBT_COALS, container.getItem(0).getTag());
                }
            }

            if (diamondsCount > 0) {
                tag.putByte(NBT_DIAMONDS_COUNT, diamondsCount);
                if (container.getItem(1).hasTag()) {
                    tag.put(NBT_DIAMONDS, container.getItem(1).getTag());
                }
            }
        }

        @Override
        public void load(CompoundTag tag) {
            super.load(tag);
            if (tag.contains(NBT_PROGRESS)) {
                progress.set(0, tag.getInt(NBT_PROGRESS));
            } else {
                progress.set(0, 0);
            }

            if (tag.contains(NBT_COALS_COUNT)) {
                var coalsCount = tag.getByte(NBT_COALS_COUNT);
                var coals = new ItemStack(Items.COAL, coalsCount);
                if (tag.contains(NBT_COALS)) {
                    coals.setTag(tag.getCompound(NBT_COALS));
                }
                container.setItem(0, coals);
            } else {
                container.setItem(0, ItemStack.EMPTY);
            }

            if (tag.contains(NBT_DIAMONDS_COUNT)) {
                var diamondsCount = tag.getByte(NBT_DIAMONDS_COUNT);
                var diamonds = new ItemStack(Items.DIAMOND, diamondsCount);
                if (tag.contains(NBT_DIAMONDS)) {
                    diamonds.setTag(tag.getCompound(NBT_DIAMONDS));
                }
                container.setItem(1, diamonds);
            } else {
                container.setItem(1, ItemStack.EMPTY);
            }
        }

        @Override
        protected Component getDefaultName() {
            return CompressorUi.TITLE;
        }

        @Override
        protected AbstractContainerMenu createMenu(int i, Inventory inventory) {
            return new CompressorUi(i, inventory, this, progress);
        }

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
                default -> SLOTS_FOR_SIDES;
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

        private GridNode gridNode;
        @Override
        public GridNode getGridNode() {
            if (gridNode == null) gridNode = GridNode.of(this);
            return gridNode;
        }
    }
}
