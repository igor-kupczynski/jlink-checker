package kupczynski.info.linkchecker.checker.impl.strategy;

import kupczynski.info.linkchecker.checker.api.CutOffStrategy;

public class MaxDepthCutOffStrategy implements CutOffStrategy {

	private final int maxDepth;
	
	public MaxDepthCutOffStrategy(int depth) {
		this.maxDepth = depth;
	}
	
	@Override
	public boolean shouldFollow(int depth, String uri) {
		return depth < maxDepth;
	}

}
