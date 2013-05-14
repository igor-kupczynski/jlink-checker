package info.kupczynski.linkchecker.checker;

public interface CutOffStrategy {

	boolean shouldFollow(int depth, String uri);
}
