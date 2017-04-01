package net.lyonlancer5.mcmp.kawo.modes.dk;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.lyonlancer5.mcmp.kawo.modes.StrategyUserHelper;

public class FreedomDKDelegate extends DKDelegate.Impl<LeverActivateStrategy> {

	public FreedomDKDelegate(EntityModeDoorKeeper mode, StrategyUserHelper<LeverActivateStrategy> subHelper) {
		super(mode, subHelper);
	}

	@Override
	public boolean checkBlock(int pMode, int px, int py, int pz) {
		return getCurrentStrategy().checkBlock(pMode, px, py, pz);
	}

	@Override
	public boolean executeBlock(int pMode, int px, int py, int pz) {
		return getCurrentStrategy().executeBlock(pMode, px, py, pz);
	}

	@Override
	public TaskState handleHealthUpdate(LMM_EntityLittleMaid maid, int maidMode, byte par1) {
		for (LeverActivateStrategy strategy : helper.getStrategies()) {
			if (strategy.handleHealthUpdate(maid, maidMode, par1) == TaskState.BREAK) {
				return TaskState.BREAK;
			}
		}
		return TaskState.CONTINUE;
	}

	@Override
	public boolean shouldStrategy() {
		return mode.owner.isFreedom();
	}

	@Override
	public void updateTask(LMM_EntityLittleMaid maid, int maidMode) {
		getCurrentStrategy().updateTask(maid, maidMode);
	}

}
