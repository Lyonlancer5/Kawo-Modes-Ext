package net.lyonlancer5.mcmp.kawo.modes.dk;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.lyonlancer5.mcmp.kawo.modes.DependencyStrategy;
import net.lyonlancer5.mcmp.unmapi.lib.future.BlockPos;
import net.minecraft.block.BlockDoor;
import net.minecraft.init.Blocks;

public interface DoorActivateStrategy extends DependencyStrategy {
	public abstract class Impl extends DependencyStrategy.DefaultImpl implements DoorActivateStrategy {
		protected final EntityModeDoorKeeper doorKeeper;

		protected BlockPos target = null;

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
			BlockDoor door = (BlockDoor) Blocks.wooden_door;
			LMM_EntityLittleMaid maid = doorKeeper.owner;
			target = null;
			if (validateBlock(px, py, pz)) {
				door.onBlockActivated(maid.worldObj, px, py, pz, maid.maidAvatar, 0, (float) maid.posX,
						(float) maid.posY, (float) maid.posZ);
				return true;
			}
			return false;
		}

		@Override
		public void notifyDependencyStrategyChanged() {
			stopStrategy();
			onChangeStrategy();
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

		protected abstract boolean validateBlock(int px, int py, int pz);
	}

	public boolean checkBlock(int pMode, int px, int py, int pz);

	public boolean executeBlock(int pMode, int px, int py, int pz);

	public void updateTask(LMM_EntityLittleMaid maid, int maidMode);
}
