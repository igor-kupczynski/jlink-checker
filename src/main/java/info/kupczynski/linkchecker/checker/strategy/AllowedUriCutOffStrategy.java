package info.kupczynski.linkchecker.checker.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import info.kupczynski.linkchecker.checker.CutOffStrategy;

public class AllowedUriCutOffStrategy implements CutOffStrategy {

	private final Pattern allowed;

	public AllowedUriCutOffStrategy(Pattern allowed) {
		this.allowed = allowed;
	}

	@Override
	public boolean shouldFollow(int depth, String uri) {
		Matcher m = allowed.matcher(uri);
		return m.matches();
	}

}
