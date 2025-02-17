package com.jetpacker06.CreateBrokenBad.block;

import com.jetpacker06.CreateBrokenBad.register.CBBBlockEntities;
import com.jetpacker06.CreateBrokenBad.register.AllCustomTriggerAdvancements;
import com.jetpacker06.CreateBrokenBad.register.AllSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.stream.Stream;

public class BrassCallBellBlock extends BaseEntityBlock {
    public BrassCallBellBlock(Properties p_49795_) {
        super(p_49795_);
    }
    public static BooleanProperty DOWN = BooleanProperty.create("down");

    @Override
    @SuppressWarnings("deprecation")
    public @NotNull VoxelShape getShape(BlockState pState, @NotNull BlockGetter pLevel, @NotNull BlockPos pPos, @NotNull CollisionContext pContext) {
        if (pState.getValue(DOWN)) {
            return downShape;
        } else {
            return upShape;
        }
    }

    private static final VoxelShape upShape = Stream.of(
            Block.box(7.5, 2.25, 7.5, 8.5, 4, 8.5),
            Block.box(7, 1, 7, 9, 3, 9),
            Block.box(6, 0, 6, 10, 1, 10)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

    private static final VoxelShape downShape = Stream.of(
            Block.box(7.5, 2.25, 7.5, 8.5, 3.25, 8.5),
            Block.box(7, 1, 7, 9, 3, 9),
            Block.box(6, 0, 6, 10, 1, 10)
    ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockState getStateForPlacement(BlockPlaceContext pContext) {
        return this.defaultBlockState().setValue(DOWN, false);
    }

    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(DOWN);
    }

    @ParametersAreNonnullByDefault
    @Override
    @SuppressWarnings("deprecation")
    public @NotNull InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
        if (pPlayer instanceof ServerPlayer) {
            AllCustomTriggerAdvancements.DING.trigger((ServerPlayer) pPlayer);
        }
        pState = pState.setValue(DOWN, true);
        pLevel.setBlock(pPos, pState, 3);
        pLevel.playSound(pPlayer,pPos, AllSoundEvents.BRASS_CALL_BELL_DING.get(), SoundSource.BLOCKS, 2f, 1f);
        return InteractionResult.sidedSuccess(pLevel.isClientSide);
    }
    @ParametersAreNonnullByDefault
    @Override
    @SuppressWarnings("deprecation")
    public @NotNull RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    @ParametersAreNonnullByDefault
    public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
        return new BrassCallBellBlockEntity(CBBBlockEntities.BRASS_CALL_BELL.get(), pPos, pState);
    }

    @Override
    @ParametersAreNonnullByDefault
    @SuppressWarnings("deprecation")
    public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
        pLevel.removeBlockEntity(pPos);
        super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
    }

    @Nullable
    @ParametersAreNonnullByDefault
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
        return createTickerHelper(pBlockEntityType, CBBBlockEntities.BRASS_CALL_BELL.get(), BrassCallBellBlockEntity::tick);
    }
    public static class Trapped extends BrassCallBellBlock {

        public Trapped(Properties p_49795_) {
            super(p_49795_);
        }
        @Override
        @SuppressWarnings("deprecation")
        public int getSignal(BlockState pBlockState, @NotNull BlockGetter pBlockAccess, @NotNull BlockPos pPos, @NotNull Direction pSide) {
            return pBlockState.getValue(DOWN) ? 15 : 0;
        }
    }
}