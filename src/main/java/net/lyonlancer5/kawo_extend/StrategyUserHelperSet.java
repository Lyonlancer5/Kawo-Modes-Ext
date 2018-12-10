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
package net.lyonlancer5.kawo_extend;

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
		for (StrategyUserHelper<?> helper : helpers)
			add(helper);
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

		for (StrategyUserHelper<?> helper : helpers)
			helper.checkChanged();

	}
}
