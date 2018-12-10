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
import net.lyonlancer5.kawo_extend.modes.Strategy;
import net.lyonlancer5.kawo_extend.modes.StrategyUserHelper;

public class StrategyDelegate<T extends Strategy> extends Strategy {

	public final StrategyUserHelper<T> helper;
	protected final EntityModeDoorKeeper mode;

	public StrategyDelegate(EntityModeDoorKeeper mode, StrategyUserHelper<T> subHelper) {
		this.mode = mode;
		this.helper = subHelper;
	}

	public T getCurrentStrategy() {
		return helper.getCurrentStrategy();
	}

	public boolean checkBlock(int pMode, int px, int py, int pz) {
		return false;
	}

	public boolean executeBlock(int pMode, int px, int py, int pz) {
		return false;
	}

	public TaskState handleHealthUpdate(LMM_EntityLittleMaid maid, int maidMode, byte par1) {
		return null;
	}

	public void updateTask(LMM_EntityLittleMaid maid, int maidMode) {
	}
}