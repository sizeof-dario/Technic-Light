package com.techniclight.techniclightmod;

import org.jspecify.annotations.Nullable;

import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ReceiverModBlock extends BaseEntityBlock
{
	private static final VoxelShape SHAPE = Block.column(16.0, 0.0, 2.0);
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

	@Override
	protected VoxelShape getShape(final BlockState state, final BlockGetter level, final BlockPos pos, final CollisionContext context) {
		return SHAPE;
	}

    public ReceiverModBlock(final BlockBehaviour.Properties properties) {
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
	}

    @Override
	protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder)
    {
		builder.add(POWERED);
    }

	/*
	@Override
	protected InteractionResult useWithoutItem(BlockState state, final Level level, final BlockPos pos, final Player player, final BlockHitResult hitResult) {
		if (!player.getAbilities().mayBuild) {
			return InteractionResult.PASS;
		}

		state = state.cycle(POWERED);
		float pitch = state.getValue(POWERED) == true ? 0.55F : 0.5F;
		level.playSound(player, pos, SoundEvents.COMPARATOR_CLICK, SoundSource.BLOCKS, 0.3F, pitch);
		level.setBlock(pos, state, 2);
		return InteractionResult.SUCCESS;
	}*/

	// Entity stuff

	@Override
	public @Nullable BlockEntity newBlockEntity(BlockPos worldPosition, BlockState blockState) {
		return new ReceiverModBlockEntity(worldPosition, blockState);
	}

	@Override
	protected MapCodec<? extends BaseEntityBlock> codec() {
		return simpleCodec(ReceiverModBlock::new);
	}

	// redstone

	@Override
	protected boolean isSignalSource(BlockState state) {
		// Dice a Minecraft che questo blocco è in grado di emettere un segnale
		return true;
	}

	@Override
	protected int getSignal(BlockState state, BlockGetter level, BlockPos pos, Direction direction) {
		// Se l'attributo POWERED è true, emette il massimo segnale (15), altrimenti 0
		return state.getValue(POWERED) ? 15 : 0;
}
}
