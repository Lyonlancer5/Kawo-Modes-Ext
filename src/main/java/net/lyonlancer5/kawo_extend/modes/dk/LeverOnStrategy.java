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

import java.util.Random;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.minecraft.block.BlockLever;
import net.minecraft.entity.player.EntityPlayer;

public class LeverOnStrategy extends LeverActivateStrategy {

	public LeverOnStrategy(EntityModeDoorKeeper doorKeeper) {
		super(doorKeeper);
	}

	@Override
	public TaskState handleHealthUpdate(LMM_EntityLittleMaid maid, int maidMode, byte par1) {
		switch (par1) {
		case 80:
			Random rng = maid.getRNG();
			int r = rng.nextInt() % 3 + 2;
			for (int i = 0; i < r; ++i) {
				double d0 = rng.nextGaussian() * 0.02D;
				double d1 = rng.nextGaussian() * 0.02D;
				double d2 = rng.nextGaussian() * 0.02D;
				maid.worldObj.spawnParticle("heart", maid.posX + rng.nextFloat() * maid.width * 2.0F - maid.width,
						maid.posY + 0.5D + rng.nextFloat() * maid.height,
						maid.posZ + rng.nextFloat() * maid.width * 2.0F - maid.width, d0, d1, d2);
			}
			return TaskState.BREAK;
		}
		return super.handleHealthUpdate(maid, maidMode, par1);
	}

	@Override
	public boolean shouldStrategy() {
		LMM_EntityLittleMaid maid = doorKeeper.owner;
		EntityPlayer player = maid.getMaidMasterEntity();
		if (maid == null || player == null)
			return false;

		if (maid.getDistanceSqToEntity(player) > 8 * 8)
			return false;

		if (!maid.canEntityBeSeen(player))
			return false;

		return true;
	}

	@Override
	public void startStrategy() {
		LMM_EntityLittleMaid maid = doorKeeper.owner;
		maid.worldObj.setEntityState(maid, (byte) 80);
		super.startStrategy();
	}

	@Override
	protected boolean validateLeverState(int px, int py, int pz, LMM_EntityLittleMaid maid, BlockLever lever) {
		return !isLeverOn(px, py, pz, maid, lever);
	}

}
