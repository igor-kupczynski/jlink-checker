package kupczynski.info.linkchecker.checker.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import kupczynski.info.linkchecker.checker.api.CutOffStrategy;
import kupczynski.info.linkchecker.checker.api.UriService;
import kupczynski.info.linkchecker.checker.api.UriServiceFactory;
import kupczynski.info.linkchecker.checker.impl.strategy.AllowedUriCutOffStrategy;
import kupczynski.info.linkchecker.checker.impl.strategy.MaxDepthCutOffStrategy;
import kupczynski.info.linkchecker.checker.impl.strategy.MultiCutOffStrategy;

public class DefaulUriServiceFactory implements UriServiceFactory {

	public static final int THREAD_COUNT = 16;
	private final String baseUri;

	public DefaulUriServiceFactory(String baseUri) {
		this.baseUri = baseUri;

	}

	@Override
	public UriService createUriService() {

		final ExecutorService executorService = makeExecutorService();
		final CutOffStrategy cutOffStrategy = makeCutOffStrategy();

		return new UriServiceImpl(executorService, cutOffStrategy);
	}

	protected ExecutorService makeExecutorService() {
		return Executors.newFixedThreadPool(THREAD_COUNT);
	}

	protected CutOffStrategy makeCutOffStrategy() {

		StringBuilder allowed = new StringBuilder();
		for (int idx = 0; idx < baseUri.length(); idx++) {
			char c = baseUri.charAt(idx);
			if (c == '.') {
				allowed.append("\\.");
			} else {
				allowed.append(c);
			}
		}
		if (allowed.lastIndexOf("/") != allowed.length() - 1) {
			allowed.append("/");
		}
		allowed.append(".*");

		return new MultiCutOffStrategy(new MaxDepthCutOffStrategy(9),
				new AllowedUriCutOffStrategy(
						Pattern.compile(allowed.toString())));
	}

}
