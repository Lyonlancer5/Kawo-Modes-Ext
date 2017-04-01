package net.lyonlancer5.mcmp.kawo.modes.dk;
import littleMaidMobX.LMM_EntityLittleMaid;
import net.lyonlancer5.mcmp.unmapi.lib.future.Vec3i;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.init.Blocks;

public class DoorCloseStrategy extends DoorActivateStrategy.Impl {

	private static final int distToClose = 7 * 7;

	public DoorCloseStrategy(EntityModeDoorKeeper doorKeeper) {
		super(doorKeeper);
	}

	@Override
	public boolean shouldStrategy() {
		return true;
	}

	@Override
	protected boolean validateBlock(int px, int py, int pz) {
		LMM_EntityLittleMaid maid = doorKeeper.owner;
		if (maid.getMaidMasterEntity() == null) {
			return false;
		}

		Block block = maid.worldObj.getBlock(px, py, pz);
		if (block != Blocks.wooden_door) {
			return false;
		}

		Vec3i checkPos = new Vec3i(px, py, pz);
		double distanceSq = checkPos.distanceSq(new Vec3i(maid.getMaidMasterEntity()));

		BlockDoor door = (BlockDoor) Blocks.wooden_door;
		if (distanceSq > distToClose) {
			if (door.func_150015_f(maid.worldObj, px, py, pz)) {
				return true;
			}
		}
		return false;
	}

}
