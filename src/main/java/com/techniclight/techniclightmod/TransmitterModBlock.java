package com.techniclight.techniclightmod;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
public class TransmitterModBlock extends BaseEntityBlock
{
	private static final VoxelShape SHAPE = Block.column(16.0, 0.0, 2.0);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	@Override
	protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
		return SHAPE;
	}

    public TransmitterModBlock(final BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
	}

    @Override
	protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder)
    {
		builder.add(POWERED);
    }

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
	if (!level.isClientSide() && level.getBlockEntity(pos) instanceof TransmitterModBlockEntity transmitterModBlockEntity) {
		player.openMenu(transmitterModBlockEntity);
	}

	return InteractionResult.SUCCESS;
	}

	// Entity stuff

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
		return new TransmitterModBlockEntity(worldPosition, blockState);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(TransmitterModBlock::new);
	}

	// from redstone lamp actually

@Override
protected void neighborChanged(
    final BlockState state, final Level level, final BlockPos pos, final Block block, final @Nullable Orientation orientation, final boolean movedByPiston
	) {
		if (!level.isClientSide()) {
			boolean isPowered = state.getValue(POWERED);
			boolean hasSignal = level.hasNeighborSignal(pos);

			if (isPowered != hasSignal) {
				if (isPowered) {
					// Se si deve spegnere, prenota il tick per il delay
					level.scheduleTick(pos, this, 4);
				} else {
					// Se si deve accendere, lo fa subito
					BlockState nextState = state.setValue(POWERED, true);
					level.setBlock(pos, nextState, 2);
					
					// Notifica il Receiver
					syncWithReceiver(level, pos, nextState);
				}
			}
		}
	}

	@Override
	protected void tick(BlockState state, ServerLevel level, BlockPos pos, net.minecraft.util.RandomSource random) {
		if (state.getValue(POWERED) && !level.hasNeighborSignal(pos)) {
			// Spegne il Transmitter dopo i 4 tick
			BlockState nextState = state.setValue(POWERED, false);
			level.setBlock(pos, nextState, 2);
			
			// Notifica il Receiver dello spegnimento!
			syncWithReceiver(level, pos, nextState);
		}
	}

	private void syncWithReceiver(Level level, BlockPos pos, BlockState currentState) {
    if (level.getBlockEntity(pos) instanceof TransmitterModBlockEntity be) {
        BlockPos receiverPos = be.getCompanionPos();
        
        if (receiverPos != null) {
            BlockState receiverState = level.getBlockState(receiverPos);
            
            if (receiverState.hasProperty(POWERED)) {
                BlockState updatedReceiverState = receiverState.setValue(POWERED, currentState.getValue(POWERED));
                
                // 1. Cambia lo stato del blocco
                level.setBlock(receiverPos, updatedReceiverState, 3);
                
                // 2. AGGIUNGI QUESTA RIGA: Avvisa i blocchi adiacenti al Receiver di controllare la redstone!
                level.updateNeighborsAt(receiverPos, receiverState.getBlock());
            }
        }
    }
}

}