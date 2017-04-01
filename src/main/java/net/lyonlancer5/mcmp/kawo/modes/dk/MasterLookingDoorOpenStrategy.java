package net.lyonlancer5.mcmp.kawo.modes.dk;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.lyonlancer5.mcmp.unmapi.lib.future.BlockPos;
import net.lyonlancer5.mcmp.unmapi.lib.future.Vec3i;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

public class MasterLookingDoorOpenStrategy extends DoorActivateStrategy.Impl {

	private static final int distToOpen = 6 * 6;

	public MasterLookingDoorOpenStrategy(EntityModeDoorKeeper doorKeeper) {
		super(doorKeeper);
	}

	@Override
	public boolean shouldStrategy() {
		LMM_EntityLittleMaid maid = doorKeeper.owner;
		BlockPos mstLookAtPos = getBlockPositionMasterLookAt();
		if (mstLookAtPos != null) {
			double distanceSq = mstLookAtPos.distanceSq(new Vec3i(maid.getMaidMasterEntity()));
			if (distanceSq <= distToOpen) {
				int px = mstLookAtPos.getX();
				int py = mstLookAtPos.getY();
				int pz = mstLookAtPos.getZ();
				Block blockId = maid.worldObj.getBlock(px, py, pz);
				if (blockId != Blocks.wooden_door) {
					return false;
				}
				BlockDoor door = (BlockDoor) Blocks.wooden_door;
				if (!door.func_150015_f(maid.worldObj, px, py, pz)) {
					return true;
				}
			}
		}
		return false;
	}

	private BlockPos getBlockPositionMasterLookAt() {
		EntityPlayer player = doorKeeper.owner.getMaidMasterEntity();
		if (player == null) {
			return null;
		}
		MovingObjectPosition mop;
		{
			float rpt = 1.0f;
			double distance = 8;
			Vec3 vec3 = player.getPosition(rpt);
			vec3 = vec3.addVector(0, player.getEyeHeight(), 0);
			Vec3 vec31 = player.getLook(rpt);
			Vec3 vec32 = vec3.addVector(vec31.xCoord * distance, vec31.yCoord * distance, vec31.zCoord * distance);
			mop = player.worldObj.rayTraceBlocks(vec3, vec32);
		}
		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK) {
			return new BlockPos(mop.blockX, mop.blockY, mop.blockZ);
		}
		return null;
	}

	@Override
	protected boolean validateBlock(int px, int py, int pz) {
		LMM_EntityLittleMaid maid = doorKeeper.owner;
		Block blockId = maid.worldObj.getBlock(px, py, pz);
		if (blockId != Blocks.wooden_door) {
			return false;
		}
		BlockPos checkPos = new BlockPos(px, py, pz);
		double distanceSq = checkPos.distanceSq(new Vec3i(maid.getMaidMasterEntity()));

		BlockDoor door = (BlockDoor) Blocks.wooden_door;
		if (distanceSq <= distToOpen) {
			BlockPos mstLookAtPos = getBlockPositionMasterLookAt();
			if (mstLookAtPos != null && mstLookAtPos.equals(checkPos)) {
				if (!door.func_150015_f(maid.worldObj, px, py, pz)) {
					return true;
				}
			}
		}
		return false;
	}

}
