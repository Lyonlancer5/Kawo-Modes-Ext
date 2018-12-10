/***************************************************************************\
* Copyright 2018 Lance David Selga [Lyonlancer5]                            *
*                                                                           *
* Licensed under the Apache License, Version 2.0 (the "License");           *
* you may not use this file except in compliance with the License.          *
* You may obtain a copy of the License at                                   *
*                                                                           *
*     http://www.apache.org/licenses/LICENSE-2.0                            *
*                                                                           *
* Unless required by applicable law or agreed to in writing, software       *
* distributed under the License is distributed on an "AS IS" BASIS,         *
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  *
* See the License for the specific language governing permissions and       *
* limitations under the License.                                            *
\***************************************************************************/
package net.lyonlancer5.kawo_extend.modes.dk;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;

public class MasterLookingDoorOpenStrategy extends DoorActivateStrategy {

	private static final int distToOpen = 6 * 6;

	public MasterLookingDoorOpenStrategy(EntityModeDoorKeeper doorKeeper) {
		super(doorKeeper);
	}

	@Override
	public boolean shouldStrategy() {
		LMM_EntityLittleMaid maid = doorKeeper.owner;
		int[] mstLookAtPos = getBlockPositionMasterLookAt();
		if (mstLookAtPos != null) {
			double distanceSq = maid.getMaidMasterEntity().getDistanceSq(mstLookAtPos[0], mstLookAtPos[1],
					mstLookAtPos[2]);
			if (distanceSq <= distToOpen) {
				Block blockId = maid.worldObj.getBlock(mstLookAtPos[0], mstLookAtPos[1], mstLookAtPos[2]);
				if (blockId != Blocks.wooden_door)
					return false;

				BlockDoor door = (BlockDoor) Blocks.wooden_door;
				if (!door.func_150015_f(maid.worldObj, mstLookAtPos[0], mstLookAtPos[1], mstLookAtPos[2]))
					return true;

			}
		}
		return false;
	}

	private int[] getBlockPositionMasterLookAt() {
		EntityPlayer player = doorKeeper.owner.getMaidMasterEntity();
		if (player == null)
			return null;

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

		if (mop != null && mop.typeOfHit == MovingObjectType.BLOCK)
			return new int[] { mop.blockX, mop.blockY, mop.blockZ };

		return null;
	}

	@Override
	protected boolean validateBlock(int px, int py, int pz) {
		LMM_EntityLittleMaid maid = doorKeeper.owner;
		Block blockId = maid.worldObj.getBlock(px, py, pz);
		if (blockId != Blocks.wooden_door)
			return false;

		int[] checkPos = new int[] { px, py, pz };
		double distanceSq = maid.getMaidMasterEntity().getDistanceSq(px, py, pz);
		BlockDoor door = (BlockDoor) Blocks.wooden_door;
		if (distanceSq <= distToOpen) {
			int[] mstLookAtPos = getBlockPositionMasterLookAt();
			if (mstLookAtPos != null && mstLookAtPos.equals(checkPos))
				if (!door.func_150015_f(maid.worldObj, px, py, pz))
					return true;

		}
		return false;
	}

}
