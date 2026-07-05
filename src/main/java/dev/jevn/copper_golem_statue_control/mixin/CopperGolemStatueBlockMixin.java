package dev.jevn.copper_golem_statue_control.mixin;

import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CopperGolemStatueBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.redstone.Orientation;

@Mixin(CopperGolemStatueBlock.class)
public class CopperGolemStatueBlockMixin extends Block {
    public CopperGolemStatueBlockMixin(Properties properties) {
        super(properties);
    }

    @Override
    protected void neighborChanged(
            final BlockState state,
            final Level level,
            final BlockPos pos,
            final Block block,
            final @Nullable Orientation orientation,
            final boolean movedByPiston
    ) {
        if (level instanceof ServerLevel serverLevel) {
            this.updatePose(state, serverLevel, pos);
        }
    }


    private void updatePose(BlockState state, ServerLevel level, BlockPos pos) {
        if (level.hasNeighborSignal(pos))
        {
            int powerLevel = level.getBestNeighborSignal(pos);

            CopperGolemStatueBlock.Pose currentPose = state.getValue(CopperGolemStatueBlock.POSE);
            CopperGolemStatueBlock.Pose pose = this.getPoseFromPowerLevel(powerLevel);

            if (pose != null && pose != currentPose) {
                level.playSound((Entity)null, pos, SoundEvents.COPPER_GOLEM_BECOME_STATUE, SoundSource.BLOCKS);
                level.setBlock(pos, state.setValue(CopperGolemStatueBlock.POSE, pose), 3);
            }
        }
    }

    private @Nullable CopperGolemStatueBlock.Pose getPoseFromPowerLevel(int powerLevel) {
        if (powerLevel == 0 || powerLevel > CopperGolemStatueBlock.Pose.values().length) {
            return null;
        }
        return CopperGolemStatueBlock.Pose.values()[powerLevel - 1];
    }


}
