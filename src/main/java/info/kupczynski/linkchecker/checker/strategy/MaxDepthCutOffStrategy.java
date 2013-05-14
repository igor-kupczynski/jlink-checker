package info.kupczynski.linkchecker.checker.strategy;

import info.kupczynski.linkchecker.checker.CutOffStrategy;

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
