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
import net.minecraft.init.Blocks;

public class DoorCloseStrategy extends DoorActivateStrategy {

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
		if (maid.getMaidMasterEntity() == null)
			return false;

		Block block = maid.worldObj.getBlock(px, py, pz);
		if (block != Blocks.wooden_door)
			return false;

		if (maid.getMaidMasterEntity().getDistanceSq(px, py, pz) > distToClose)
			if (((BlockDoor) Blocks.wooden_door).func_150015_f(maid.worldObj, px, py, pz))
				return true;

		return false;
	}

}
