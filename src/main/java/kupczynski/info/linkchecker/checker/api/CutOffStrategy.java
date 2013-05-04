package kupczynski.info.linkchecker.checker.api;

public interface CutOffStrategy {

	boolean shouldFollow(int depth, String uri);
}
