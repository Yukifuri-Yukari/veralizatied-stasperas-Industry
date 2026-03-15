package yukifuri.mc.vsindustry.block;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import yukifuri.mc.vsindustry.VSIndustry;
import yukifuri.mc.vsindustry.api.level.blockentity.BaseBlockEntity;
import yukifuri.mc.vsindustry.api.level.blockentity.SimpleBlockWithEntity;
import yukifuri.mc.vsindustry.level.node.GridNode;
import yukifuri.mc.vsindustry.level.node.Node;
import yukifuri.mc.vsindustry.registries.VBlocks;

@MethodsReturnNonnullByDefault
public class Cable extends SimpleBlockWithEntity<Cable.Entity> {
    private final LoadingCache<BlockState, VoxelShape> SHAPE_CACHE = CacheBuilder.newBuilder()
            .maximumSize(128)
            .build(CacheLoader.from(this::calculateShape));

    private static final VoxelShape
            SHAPE_CORE  = Block.box(4, 4, 4, 12, 12, 12),
            SHAPE_UP    = Block.box(4, 12, 4, 12, 16, 12),
            SHAPE_DOWN  = Block.box(4, 0, 4, 12, 4, 12),
            SHAPE_NORTH = Block.box(4, 4, 0, 12, 12, 4),
            SHAPE_SOUTH = Block.box(4, 4, 12, 12, 12, 16),
            SHAPE_WEST  = Block.box(0, 4, 4, 4, 12, 12),
            SHAPE_EAST  = Block.box(12, 4, 4, 16, 12, 12);

    public static final BooleanProperty
            NORTH = BooleanProperty.create("north"),
            EAST = BooleanProperty.create("east"),
            SOUTH = BooleanProperty.create("south"),
            WEST = BooleanProperty.create("west"),
            UP = BooleanProperty.create("up"),
            DOWN = BooleanProperty.create("down");

    public Cable() {
        super(BlockBehaviour.Properties.of());
    }

    //region something about BlockState
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(NORTH, EAST, SOUTH, WEST, UP, DOWN);
    }

    @Override
    public float getShadeBrightness(BlockState state, BlockGetter level, BlockPos pos) {
        return 1.0F; // makes cable won't shadow too large
    }

    @Override
    public boolean propagatesSkylightDown(BlockState state, BlockGetter level, BlockPos pos) {
        return true; // makes skylight pass through
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return SHAPE_CACHE.getUnchecked(state);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighborState, LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        // direction is the neighbor's direction that is relative to this block
        // if neighbor changes, update state
        return state.setValue(getProperty(direction), canConnectTo(level, neighborPos));
    }

    private BooleanProperty getProperty(Direction direction) {
        return switch (direction) {
            case UP -> UP;
            case DOWN -> DOWN;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }

    private boolean canConnectTo(BlockGetter level, BlockPos neighborPos) {
        return level.getBlockState(neighborPos).getBlock() instanceof Cable;
    }

    private VoxelShape calculateShape(BlockState state) {
        VoxelShape shape = SHAPE_CORE;

        if (state.getValue(UP)) shape = Shapes.or(shape, SHAPE_UP);
        if (state.getValue(DOWN)) shape = Shapes.or(shape, SHAPE_DOWN);
        if (state.getValue(NORTH)) shape = Shapes.or(shape, SHAPE_NORTH);
        if (state.getValue(SOUTH)) shape = Shapes.or(shape, SHAPE_SOUTH);
        if (state.getValue(WEST)) shape = Shapes.or(shape, SHAPE_WEST);
        if (state.getValue(EAST)) shape = Shapes.or(shape, SHAPE_EAST);

        return shape.optimize();
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return makeConnections(context.getLevel(), context.getClickedPos());
    }

    public BlockState makeConnections(Level level, BlockPos pos) {
        return defaultBlockState()
                .setValue(NORTH, canConnectTo(level, pos.north()))
                .setValue(EAST, canConnectTo(level, pos.east()))
                .setValue(SOUTH, canConnectTo(level, pos.south()))
                .setValue(WEST, canConnectTo(level, pos.west()))
                .setValue(UP, canConnectTo(level, pos.above()))
                .setValue(DOWN, canConnectTo(level, pos.below()));
    }

    private boolean canConnectTo(Level level, BlockPos neighborPos) {
        BlockState state = level.getBlockState(neighborPos);
        return state.getBlock() instanceof Cable;
    }
    //endregion

    @Override
    public void onPlace(BlockState state, Level level, BlockPos pos, BlockState oldState, boolean movedByPiston) {
        // TODO: 2026/3/7 Calling grid reload
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.is(newState.getBlock())) return;
        // TODO: 2026/3/7 Calling grid reload
    }

    @Override @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new Cable.Entity(pos, state);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state, Entity entity) { }

    public static class Entity extends BaseBlockEntity implements Node {
        public static final BlockEntityType<Entity> TYPE = FabricBlockEntityTypeBuilder
                .create(Entity::new, VBlocks.CABLE)
                .build();

        public Entity(BlockPos pos, BlockState blockState) {
            super(TYPE, pos, blockState);
        }
    }
}
