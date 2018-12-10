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

import java.util.Set;

import com.google.common.collect.Sets;

import littleMaidMobX.LMM_EntityLittleMaid;
import net.lyonlancer5.kawo_extend.Strategy;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLever;
import net.minecraft.init.Blocks;

public class LeverActivateStrategy extends Strategy {
	protected final EntityModeDoorKeeper doorKeeper;

	protected int[] target = null;
	protected Set<int[]> activatedLevers = Sets.newHashSet();

	public LeverActivateStrategy(EntityModeDoorKeeper doorKeeper) {
		this.doorKeeper = doorKeeper;
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
		BlockLever lever = (BlockLever) Blocks.lever;
		LMM_EntityLittleMaid maid = doorKeeper.owner;
		target = null;
		if (validateBlock(px, py, pz)) {
			lever.onBlockActivated(maid.worldObj, px, py, pz, maid.maidAvatar, 0, (float) maid.posX,
					(float) maid.posY, (float) maid.posZ);
			activatedLevers.add(new int[] { px, py, pz });
			return true;
		}
		return false;
	}

	@Override
	public TaskState handleHealthUpdate(LMM_EntityLittleMaid maid, int maidMode, byte par1) {
		return null;
	}

	@Override
	public void notifyDependentsOfChange() {
		stopStrategy();
		onChangeStrategy();
	}

	@Override
	public void onChangeStrategy() {
		activatedLevers = Sets.newHashSet();
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
	public void updateTask(LMM_EntityLittleMaid maid, int maidMode) {
		if (target != null) {
			double ld = maid.getDistanceSq(target[0], target[1], target[2]);
			if (ld <= 5.0D)
				stopStrategy();

		}
	}

	protected boolean isLeverOn(int px, int py, int pz, LMM_EntityLittleMaid maid, BlockLever lever) {
		return lever.isProvidingWeakPower(maid.worldObj, px, py, pz, 0) > 0;
	}

	protected boolean validateBlock(int px, int py, int pz) {
		LMM_EntityLittleMaid maid = doorKeeper.owner;
		if (maid.getMaidMasterEntity() == null)
			return false;

		if (activatedLevers.contains(new int[] { px, py, pz }))
			return false;

		Block block = maid.worldObj.getBlock(px, py, pz);
		if (block != Blocks.lever)
			return false;

		BlockLever lever = (BlockLever) Blocks.lever;
		if (validateLeverState(px, py, pz, maid, lever))
			return true;

		return false;
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

	protected boolean validateLeverState(int px, int py, int pz, LMM_EntityLittleMaid maid, BlockLever lever) {
		return false;
	}
}
