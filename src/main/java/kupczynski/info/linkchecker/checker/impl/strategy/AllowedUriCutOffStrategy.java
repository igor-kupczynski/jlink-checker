package kupczynski.info.linkchecker.checker.impl.strategy;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kupczynski.info.linkchecker.checker.CutOffStrategy;

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
