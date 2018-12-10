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

import java.util.List;

import com.google.common.collect.Lists;

/**
 * Helper module for switching between strategies during updates
 * 
 * @author Lyonlancer5
 */
public class StrategyUserHelper<S extends Strategy> {
	private S prevStrategy;
	private S currentStrategy;
	private S defaultStrategy;

	private final List<S> strategies = Lists.newArrayList();

	public <T extends S> StrategyUserHelper(T defaultStrategy) {
		this.defaultStrategy = this.currentStrategy = defaultStrategy;
	}

	public boolean add(S strategy) {
		return strategies.add(strategy);
	}

	public void addDependencyStrategy(StrategyUserHelper<?> helper) {
		for (S strategy : strategies)
			if (strategy.hasDependentsChanged())
				strategy.addDependency(helper);
	}

	public void checkChanged() {
		if (isCurrentChanged()) {
			finishPrevStrategy();
			startStrategy();
		} else
			checkDependencyStrategy();

	}

	public void checkDependencyStrategy() {
		Strategy strategy = getCurrentStrategy();
		if (strategy.hasDependentsChanged())
			strategy.notifyDependentsOfChange();
	}

	public S getCurrentStrategy() {
		return currentStrategy != null ? currentStrategy : defaultStrategy;
	}

	public Iterable<S> getStrategies() {
		return strategies;
	}

	public void updateCurrentStrategy() {
		pushPrevStrategy();
		changeCurrentStrategy();
		checkChanged();
	}

	protected void changeCurrentStrategy() {
		currentStrategy = defaultStrategy;
		for (S strategy : strategies) {
			if (strategy.shouldStrategy()) {
				currentStrategy = strategy;
				break;
			}
		}
	}

	protected void finishPrevStrategy() {
		prevStrategy.stopStrategy();
		prevStrategy.onChangeStrategy();
	}

	protected boolean isCurrentChanged() {
		return currentStrategy != prevStrategy;
	}

	protected void pushPrevStrategy() {
		prevStrategy = currentStrategy;
	}

	protected void startStrategy() {
		currentStrategy.startStrategy();
	}
}
