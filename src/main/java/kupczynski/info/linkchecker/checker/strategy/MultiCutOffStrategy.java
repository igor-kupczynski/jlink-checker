package kupczynski.info.linkchecker.checker.strategy;

import java.util.Collection;

import com.google.common.collect.ImmutableList;

import kupczynski.info.linkchecker.checker.CutOffStrategy;

public class MultiCutOffStrategy implements CutOffStrategy {

	private Collection<CutOffStrategy> strategies;

	public MultiCutOffStrategy(CutOffStrategy... items) {
		strategies = ImmutableList.copyOf(items);
	}

	@Override
	public boolean shouldFollow(int depth, String uri) {
		for (CutOffStrategy s : strategies) {
			if (!s.shouldFollow(depth, uri))
				return false;
		}
		return true;
	}

}
