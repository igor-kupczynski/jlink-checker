package info.kupczynski.linkchecker;

import java.util.Map;

import info.kupczynski.linkchecker.checker.UriService;
import info.kupczynski.linkchecker.checker.UriServiceFactory;
import info.kupczynski.linkchecker.checker.UriStatusDTO;
import info.kupczynski.linkchecker.checker.impl.DefaulUriServiceFactory;
import info.kupczynski.linkchecker.ouput.CsvUriStatusReporter;

import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Runner {

	private static final Logger logger = LoggerFactory.getLogger(Runner.class);

	private final CliOptions options;

	private Runner(CliOptions options) {
		this.options = options;
	}

	private void run() {
		UriServiceFactory factory = new DefaulUriServiceFactory(
				options.getAllowedUri());

		UriService uri = factory.createUriService();
		Map<String, UriStatusDTO> result = uri.start(options.getTarget());

		CsvUriStatusReporter reporter = new CsvUriStatusReporter(
				options.getReportFileName());
		reporter.reportUris(result.values());
	}

	public static void main(String... args) {
		CliOptions options = null;
		try {
			options = CliOptions.fromArgs(args);
		} catch (ParseException e) {
			logger.warn("Error parsing command line options", e);
			System.exit(1);
		}
		Runner runner = new Runner(options);
		runner.run();
	}

}
