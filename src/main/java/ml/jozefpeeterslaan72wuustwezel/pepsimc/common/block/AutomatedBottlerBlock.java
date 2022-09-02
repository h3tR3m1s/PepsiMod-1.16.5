package ml.jozefpeeterslaan72wuustwezel.pepsimc.common.block;

import ml.jozefpeeterslaan72wuustwezel.pepsimc.common.entity.blockentity.AutomatedBottlerEntity;
import ml.jozefpeeterslaan72wuustwezel.pepsimc.common.entity.blockentity.AutomatedProcessingBlockEntity;
import ml.jozefpeeterslaan72wuustwezel.pepsimc.common.entity.blockentity.PepsiMcBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public class AutomatedBottlerBlock extends HorizontalFacedBlock implements EntityBlock {


	private static final VoxelShape ShW = Stream.of(
			Block.box(5, 7, 10, 6, 9, 11),
			Block.box(10, 7, 10, 11, 9, 11),
			Block.box(10, 7, 5, 11, 9, 6),
			Block.box(5, 7, 5, 6, 9, 6),
			Block.box(6, 7, 11, 10, 9, 12),
			Block.box(6, 7, 4, 10, 9, 5),
			Block.box(13, 5, 6, 15, 16, 10),
			Block.box(1, 5, 6, 3, 16, 10),
			Block.box(4, 7, 6, 5, 9, 10),
			Block.box(13, 7, 10, 14, 9, 16),
			Block.box(11, 7, 6, 12, 9, 10),
			Block.box(7, 12, 7, 9, 16, 9),
			Block.box(3, 14, 6, 13, 15, 10),
			Block.box(2, 7, 10, 3, 9, 16),
			Block.box(13, 7, 0, 14, 9, 6),
			Block.box(2, 7, 0, 3, 9, 6),
			Block.box(0, 0, 0, 16, 7, 16)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();
	private static final VoxelShape ShL = Stream.of(
			Block.box(10, 7, 10, 11, 9, 11),
			Block.box(10, 7, 5, 11, 9, 6),
			Block.box(5, 7, 5, 6, 9, 6),
			Block.box(5, 7, 10, 6, 9, 11),
			Block.box(11, 7, 6, 12, 9, 10),
			Block.box(4, 7, 6, 5, 9, 10),
			Block.box(6, 5, 1, 10, 16, 3),
			Block.box(6, 5, 13, 10, 16, 15),
			Block.box(6, 7, 11, 10, 9, 12),
			Block.box(10, 7, 2, 16, 9, 3),
			Block.box(6, 7, 4, 10, 9, 5),
			Block.box(7, 12, 7, 9, 16, 9),
			Block.box(6, 14, 3, 10, 15, 13),
			Block.box(10, 7, 13, 16, 9, 14),
			Block.box(0, 7, 2, 6, 9, 3),
			Block.box(0, 7, 13, 6, 9, 14),
			Block.box(0, 0, 0, 16, 7, 16)
			).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

	public AutomatedBottlerBlock() {
		super(Properties
				.of(Material.PISTON)
				.strength(4.5f,15)
				.sound(SoundType.METAL)
				.requiresCorrectToolForDrops());
	}
	
	
	
	@SuppressWarnings("deprecation")
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState secondState, boolean p_196243_5_) {
	      if (!state.is(secondState.getBlock())) {
	         BlockEntity blockEntity = level.getBlockEntity(pos);
	         if (blockEntity instanceof AutomatedBottlerEntity automatedbottlerentity) {
				 Containers.dropContents(level, pos, automatedbottlerentity.getNNLInv());
	            level.updateNeighbourForOutputSignal(pos, this);
	         }

	         super.onRemove(state, level, pos, secondState, p_196243_5_);
	      }
	   }

	@Override
	public @NotNull InteractionResult use(BlockState state, Level world, BlockPos pos,
										  Player plr, InteractionHand hand, BlockHitResult hit) {
		if (!world.isClientSide()) {
			BlockEntity entity = world.getBlockEntity(pos);
			if(entity instanceof AutomatedBottlerEntity) {
				NetworkHooks.openGui(((ServerPlayer)plr), (AutomatedBottlerEntity)entity, pos);
			} else {
				throw new IllegalStateException("Container provider is missing!");
			}
		}

		return InteractionResult.sidedSuccess(world.isClientSide());
	}

	
	@Override 
	public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter worldIn, @NotNull BlockPos p, @NotNull CollisionContext context) {
		return switch (state.getValue(FACING)) {
			case EAST, WEST -> ShL;
			default -> ShW;
		};
	}

	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return PepsiMcBlockEntity.AUTOMATED_BOTTLER_BLOCK_ENTITY.get().create(pos, state);
	}

	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
		if (level.isClientSide()) {
			return null;
		} else {
			return (level1, pos, state1, tile) -> {
				if (tile instanceof AutomatedProcessingBlockEntity Machine) {
					Machine.tickServer();
				}
			};
		}
	}
}
