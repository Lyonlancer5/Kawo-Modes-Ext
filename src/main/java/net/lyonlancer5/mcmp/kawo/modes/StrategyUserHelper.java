package net.lyonlancer5.mcmp.kawo.modes;

import java.util.List;

import com.google.common.collect.Lists;

public class StrategyUserHelper<S extends Strategy> {
	private S prevStrategy = null;

	private S currentStrategy = null;

	private S defaultStrategy = null;

	private final List<S> strategies = Lists.newArrayList();

	public <T extends S> StrategyUserHelper(T defaultStrategy) {
		this.defaultStrategy = defaultStrategy;
		this.currentStrategy = defaultStrategy;
	}

	public boolean add(S arg0) {
		return strategies.add(arg0);
	}

	public void addDependencyStrategy(StrategyUserHelper<?> helper) {
		for (S strategy : strategies) {
			if (strategy instanceof DependencyStrategy) {
				DependencyStrategy dependencyStrategy = (DependencyStrategy) strategy;
				if (dependencyStrategy.isDependencyStrategyChanged()) {
					dependencyStrategy.addDependencyStrategy(helper);
				}
			}
		}
	}

	public void checkChanged() {
		if (isCurrentChanged()) {
			finishPrevStrategy();
			startStrategy();
		} else {
			checkDependencyStrategy();
		}
	}

	public void checkDependencyStrategy() {
		Strategy strategy = getCurrentStrategy();
		if (strategy instanceof DependencyStrategy) {
			DependencyStrategy dependencyStrategy = (DependencyStrategy) strategy;
			if (dependencyStrategy.isDependencyStrategyChanged()) {
				dependencyStrategy.notifyDependencyStrategyChanged();
			}
		}
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
