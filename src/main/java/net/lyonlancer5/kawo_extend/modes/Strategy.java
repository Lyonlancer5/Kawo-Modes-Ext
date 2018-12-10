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
package net.lyonlancer5.kawo_extend.modes;

import java.util.Set;

import com.google.common.collect.Sets;

import littleMaidMobX.LMM_EntityLittleMaid;

/**
 * Unified Strategy implementation including dependency information and delegate
 * functions
 * 
 * @author Lyonlancer5
 */
public abstract class Strategy {
	Set<StrategyUserHelper<?>> dependencies;

	public static enum TaskState {
		CONTINUE, BREAK;
	}

	/**
	 * Adds a strategy that depends on this.
	 * 
	 * @param helper The strategy
	 */
	public void addDependency(StrategyUserHelper<?> helper) {
		if (dependencies == null)
			dependencies = Sets.newHashSet();
		
		dependencies.add(helper);
	}

	/**
	 * Checks if any dependent strategies have changed since last update.
	 */
	public boolean hasDependentsChanged() {
		if (dependencies == null || dependencies.isEmpty())
			return false;

		for (StrategyUserHelper<?> helper : dependencies)
			if (helper.isCurrentChanged())
				return true;

		return false;
	}

	public void notifyDependentsOfChange() {
	}

	public void onChangeStrategy() {
	}

	public void onUpdateStrategy() {
	}

	public boolean shouldStrategy() {
		return false;
	}

	public void startStrategy() {
	}

	public void stopStrategy() {
	}

	// Unification of delegates
	public boolean checkBlock(int pMode, int px, int py, int pz) {
		return false;
	}

	public boolean executeBlock(int pMode, int px, int py, int pz) {
		return false;
	}

	public void updateTask(LMM_EntityLittleMaid maid, int maidMode) {

	}

	public TaskState handleHealthUpdate(LMM_EntityLittleMaid maid, int maidMode, byte par1) {
		return null;
	}
}