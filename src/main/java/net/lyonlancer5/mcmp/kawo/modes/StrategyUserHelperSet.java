package net.lyonlancer5.mcmp.kawo.modes;

import java.util.Iterator;
import java.util.Set;

import com.google.common.collect.Sets;

public class StrategyUserHelperSet implements Iterable<StrategyUserHelper<?>> {
	private final Set<StrategyUserHelper<?>> helpers = Sets.newHashSet();

	public StrategyUserHelperSet() {
	}

	public StrategyUserHelperSet(StrategyUserHelper<?>... helpers) {
		add(helpers);
	}

	public void add(StrategyUserHelper<?> e) {
		helpers.add(e);
	}

	public void add(StrategyUserHelper<?>... helpers) {
		for (StrategyUserHelper<?> helper : helpers) {
			add(helper);
		}
	}

	@Override
	public Iterator<StrategyUserHelper<?>> iterator() {
		return helpers.iterator();
	}

	public void updateCurrentStrategy() {
		for (StrategyUserHelper<?> helper : helpers) {
			helper.pushPrevStrategy();
			helper.changeCurrentStrategy();
		}
		for (StrategyUserHelper<?> helper : helpers) {
			helper.checkChanged();
		}
	}
}
