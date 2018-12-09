package net.lyonlancer5.kawo_extend.modes.dk;

import java.util.Set;
import com.google.common.collect.Sets;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.lyonlancer5.kawo_extend.modes.DependencyStrategy;
import net.lyonlancer5.mcmp.unmapi.lib.future.BlockPos;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.init.Blocks;
public interface LeverActivateStrategy extends DependencyStrategy {

	public abstract class Impl extends DependencyStrategy.DefaultImpl implements LeverActivateStrategy {
		protected final EntityModeDoorKeeper doorKeeper;

		protected BlockPos target = null;

		protected Set<BlockPos> activatedLevers = Sets.newHashSet();

		public Impl(EntityModeDoorKeeper doorKeeper) {
			this.doorKeeper = doorKeeper;
		}

		@Override
		public boolean checkBlock(int pMode, int px, int py, int pz) {
			if (validateBlock(px, py, pz)) {
				target = new BlockPos(px, py, pz);
				return true;
			} else {
				target = null;
				return false;
			}
		}

		@Override
		public boolean executeBlock(int pMode, int px, int py, int pz) {
			BlockLever lever = (BlockLever) Blocks.lever;
			LMM_EntityLittleMaid maid = doorKeeper.owner;
			target = null;
			if (validateBlock(px, py, pz)) {
				lever.onBlockActivated(maid.worldObj, px, py, pz, maid.maidAvatar, 0, (float) maid.posX,
						(float) maid.posY, (float) maid.posZ);
				activatedLevers.add(new BlockPos(px, py, pz));
				return true;
			}
			return false;
		}

		@Override
		public TaskState handleHealthUpdate(LMM_EntityLittleMaid maid, int maidMode, byte par1) {
			return null;
		}

		@Override
		public void notifyDependencyStrategyChanged() {
			stopStrategy();
			onChangeStrategy();
		}

		@Override
		public void onChangeStrategy() {
			activatedLevers = Sets.newHashSet();
		}

		@Override
		public void stopStrategy() {
			LMM_EntityLittleMaid maid = doorKeeper.owner;
			if (target != null) {
				maid.getNavigator().clearPathEntity();
				target = null;
			}
		}

		@Override
		public void updateTask(LMM_EntityLittleMaid maid, int maidMode) {
			if (target != null) {
				double ld = maid.getDistanceSq(target.getX(), target.getY(), target.getZ());
				if (ld <= 5.0D) {
					// 射程距離
					stopStrategy();
				}
			}
		}

		protected boolean isLeverOn(int px, int py, int pz, LMM_EntityLittleMaid maid, BlockLever lever) {
			return lever.isProvidingWeakPower(maid.worldObj, px, py, pz, 0) > 0;
		}

		protected boolean validateBlock(int px, int py, int pz) {
			LMM_EntityLittleMaid maid = doorKeeper.owner;
			if (maid.getMaidMasterEntity() == null) {
				return false;
			}

			if (activatedLevers.contains(new BlockPos(px, py, pz))) {
				return false;
			}

			Block block = maid.worldObj.getBlock(px, py, pz);
			if (block != Blocks.lever) {
				return false;
			}

			BlockLever lever = (BlockLever) Blocks.lever;
			if (validateLeverState(px, py, pz, maid, lever)) {
				return true;
			}
			return false;
		}

		protected abstract boolean validateLeverState(int px, int py, int pz, LMM_EntityLittleMaid maid,
				BlockLever lever);
	}

	public boolean checkBlock(int pMode, int px, int py, int pz);

	public boolean executeBlock(int pMode, int px, int py, int pz);

	public TaskState handleHealthUpdate(LMM_EntityLittleMaid maid, int maidMode, byte par1);

	public void updateTask(LMM_EntityLittleMaid maid, int maidMode);
}
