package net.lyonlancer5.mcmp.kawo.modes;

import java.util.Set;

import com.google.common.collect.Sets;

public interface DependencyStrategy extends Strategy {
	public static abstract class DefaultImpl extends Strategy.DefaultImpl implements DependencyStrategy {
		Set<StrategyUserHelper<?>> dependencys = Sets.newHashSet();

		@Override
		public void addDependencyStrategy(StrategyUserHelper<?> helper) {
			dependencys.add(helper);
		}

		@Override
		public boolean isDependencyStrategyChanged() {
			for (StrategyUserHelper<?> helper : dependencys) {
				if (helper.isCurrentChanged()) {
					return true;
				}
			}
			return false;
		}

		@Override
		public void notifyDependencyStrategyChanged() {
		}
	}

	public void addDependencyStrategy(StrategyUserHelper<?> helper);

	public boolean isDependencyStrategyChanged();

	/**
	 * 依存するStrategyに変化があった時のイベント
	 */
	public void notifyDependencyStrategyChanged();

}