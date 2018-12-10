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
import net.lyonlancer5.kawo_extend.Strategy;
import net.minecraft.block.BlockDoor;
import net.minecraft.init.Blocks;

public abstract class DoorActivateStrategy extends Strategy {
	protected final EntityModeDoorKeeper doorKeeper;

	protected int[] target = null;

	public DoorActivateStrategy(EntityModeDoorKeeper doorKeeper) {
		this.doorKeeper = doorKeeper;
	}

	@Override
	public void notifyDependentsOfChange() {
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
	public boolean checkBlock(int pMode, int px, int py, int pz) {
		if (validateBlock(px, py, pz)) {
			target = new int[] { px, py, pz };
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
			door.onBlockActivated(maid.worldObj, px, py, pz, maid.maidAvatar, 0, (float) maid.posX, (float) maid.posY,
					(float) maid.posZ);
			return true;
		}
		return false;
	}

	@Override
	public void updateTask(LMM_EntityLittleMaid maid, int maidMode) {
		if (target != null) {
			double ld = maid.getDistanceSq(target[0], target[1], target[2]);
			if (ld <= 5.0D)
				stopStrategy();

		}
	}

	protected abstract boolean validateBlock(int px, int py, int pz);

}
