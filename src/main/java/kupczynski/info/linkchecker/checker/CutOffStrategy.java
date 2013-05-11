package kupczynski.info.linkchecker.checker;

public interface CutOffStrategy {

	boolean shouldFollow(int depth, String uri);
}
