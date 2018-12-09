package net.lyonlancer5.kawo_extend.modes.dk;


import littleMaidMobX.LMM_EntityLittleMaid;
import net.minecraft.block.BlockLever;
import net.minecraft.entity.player.EntityPlayer;

public class LeverOffStrategy extends LeverActivateStrategy.Impl {

	private Long firstMissingTime = null;

	public LeverOffStrategy(EntityModeDoorKeeper doorKeeper) {
		super(doorKeeper);
	}

	@Override
	public void onChangeStrategy() {
		firstMissingTime = null;
		super.onChangeStrategy();
	}

	@Override
	public boolean shouldStrategy() {
		LMM_EntityLittleMaid maid = doorKeeper.owner;
		EntityPlayer player = maid.getMaidMasterEntity();
		if (maid == null || player == null) {
			return false;
		}

		if (maid.getDistanceSqToEntity(player) > 10 * 10) {
			return true;
		}
		if (maid.canEntityBeSeen(player)) {
			return false;
		}

		long time = maid.worldObj.getWorldTime();
		if (firstMissingTime == null) {
			firstMissingTime = time;
			return false;
		} else if (time - firstMissingTime < EntityModeDoorKeeper.waitMargin) {
			return false;
		}

		return true;
	}

	@Override
	protected boolean validateLeverState(int px, int py, int pz, LMM_EntityLittleMaid maid, BlockLever lever) {
		return isLeverOn(px, py, pz, maid, lever);
	}

}
