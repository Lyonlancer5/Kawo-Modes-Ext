package net.lyonlancer5.kawo_extend.modes.dk;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.lyonlancer5.kawo_extend.modes.StrategyUserHelper;

public class EscorterDKDelegate extends DKDelegate.Impl<DoorActivateStrategy> {

	public EscorterDKDelegate(EntityModeDoorKeeper mode, StrategyUserHelper<DoorActivateStrategy> subHelper) {
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
	public boolean shouldStrategy() {
		return !mode.owner.isMaidWait() && !mode.owner.isFreedom();
	}

	@Override
	public void updateTask(LMM_EntityLittleMaid maid, int maidMode) {
		getCurrentStrategy().updateTask(maid, maidMode);
	}

}
