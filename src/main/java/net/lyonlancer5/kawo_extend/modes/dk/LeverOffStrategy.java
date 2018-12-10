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
import net.minecraft.block.BlockLever;
import net.minecraft.entity.player.EntityPlayer;

public class LeverOffStrategy extends LeverActivateStrategy {

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
		if (maid == null || player == null)
			return false;

		if (maid.getDistanceSqToEntity(player) > 10 * 10)
			return true;

		if (maid.canEntityBeSeen(player))
			return false;

		long time = maid.worldObj.getWorldTime();
		if (firstMissingTime == null) {
			firstMissingTime = time;
			return false;
		} else if (time - firstMissingTime < EntityModeDoorKeeper.waitMargin)
			return false;

		return true;
	}

	@Override
	protected boolean validateLeverState(int px, int py, int pz, LMM_EntityLittleMaid maid, BlockLever lever) {
		return isLeverOn(px, py, pz, maid, lever);
	}

}
