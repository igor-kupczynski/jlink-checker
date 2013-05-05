package kupczynski.info.linkchecker;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Pattern;

import kupczynski.info.linkchecker.checker.api.CutOffStrategy;
import kupczynski.info.linkchecker.checker.api.UriService;
import kupczynski.info.linkchecker.checker.api.UriStatusDTO;
import kupczynski.info.linkchecker.checker.impl.UriServiceImpl;
import kupczynski.info.linkchecker.checker.impl.strategy.AllowedUriCutOffStrategy;
import kupczynski.info.linkchecker.checker.impl.strategy.MaxDepthCutOffStrategy;
import kupczynski.info.linkchecker.checker.impl.strategy.MultiCutOffStrategy;
import kupczynski.info.linkchecker.ouput.CsvUriStatusReporter;

public class Runner {

	public static final int MAX_DEPTH = 9;
	public static final String allowed = "http://masz-prawo\\.info/.*";

	public static void main(String[] args) {

		ExecutorService exec = Executors.newFixedThreadPool(32);

		CutOffStrategy cutoff = new MultiCutOffStrategy(
				new MaxDepthCutOffStrategy(9), new AllowedUriCutOffStrategy(
						Pattern.compile(allowed)));

		UriService uri = new UriServiceImpl(exec, cutoff);
		Map<String, UriStatusDTO> result = uri.start("http://masz-prawo.info/");

		CsvUriStatusReporter reporter = new CsvUriStatusReporter("report.csv");
		reporter.reportUris(result.values());
	}
}
