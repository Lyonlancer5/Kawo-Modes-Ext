package net.lyonlancer5.kawo_extend.modes.dk;


import littleMaidMobX.LMM_EntityLittleMaid;
import net.lyonlancer5.kawo_extend.modes.Strategy;
import net.lyonlancer5.kawo_extend.modes.StrategyUserHelper;

public interface DKDelegate extends Strategy {
	public static abstract class Impl<T extends Strategy> extends Strategy.DefaultImpl implements DKDelegate {
		protected final EntityModeDoorKeeper mode;

		public final StrategyUserHelper<T> helper;

		public Impl(EntityModeDoorKeeper mode, StrategyUserHelper<T> subHelper) {
			this.mode = mode;
			helper = subHelper;
		}

		@Override
		public boolean checkBlock(int pMode, int px, int py, int pz) {
			return false;
		}

		@Override
		public boolean executeBlock(int pMode, int px, int py, int pz) {
			return false;
		}

		public T getCurrentStrategy() {
			return helper.getCurrentStrategy();
		}

		@Override
		public TaskState handleHealthUpdate(LMM_EntityLittleMaid maid, int maidMode, byte par1) {
			return null;
		}

		@Override
		public void updateTask(LMM_EntityLittleMaid maid, int maidMode) {
		}

	}

	public abstract boolean checkBlock(int pMode, int px, int py, int pz);

	public abstract boolean executeBlock(int pMode, int px, int py, int pz);

	public TaskState handleHealthUpdate(LMM_EntityLittleMaid maid, int maidMode, byte par1);

	public abstract void updateTask(LMM_EntityLittleMaid maid, int maidMode);

}