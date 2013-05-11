package kupczynski.info.linkchecker.checker.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import kupczynski.info.linkchecker.checker.CutOffStrategy;
import kupczynski.info.linkchecker.checker.UriService;
import kupczynski.info.linkchecker.checker.UriServiceFactory;
import kupczynski.info.linkchecker.checker.strategy.AllowedUriCutOffStrategy;
import kupczynski.info.linkchecker.checker.strategy.MaxDepthCutOffStrategy;
import kupczynski.info.linkchecker.checker.strategy.MultiCutOffStrategy;

public class DefaulUriServiceFactory implements UriServiceFactory {

	public static final int THREAD_COUNT = 16;
	private final String allowed;

	public DefaulUriServiceFactory(String allowed) {
		this.allowed = allowed;

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

		return new MultiCutOffStrategy(new MaxDepthCutOffStrategy(9),
				new AllowedUriCutOffStrategy(
						Pattern.compile(allowed.toString())));
	}

}
