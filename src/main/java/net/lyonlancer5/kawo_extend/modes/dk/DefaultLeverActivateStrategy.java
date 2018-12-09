package net.lyonlancer5.kawo_extend.modes.dk;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.minecraft.block.BlockLever;

public class DefaultLeverActivateStrategy extends LeverActivateStrategy.Impl {

	public DefaultLeverActivateStrategy(EntityModeDoorKeeper doorKeeper) {
		super(doorKeeper);
	}

	@Override
	public boolean shouldStrategy() {
		return true;
	}

	@Override
	public void startStrategy() {
		doorKeeper.owner.getNavigator().clearPathEntity();
		super.startStrategy();
	}

	@Override
	protected boolean validateLeverState(int px, int py, int pz, LMM_EntityLittleMaid maid, BlockLever lever) {
		return false;
	}

}
