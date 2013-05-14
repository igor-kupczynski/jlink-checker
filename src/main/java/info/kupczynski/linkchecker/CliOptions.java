package info.kupczynski.linkchecker;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class CliOptions {

	private static final String TARGET_URI = "targetUri";
	private static final String REPORT_FILE_NAME = "reportFileName";
	private static final String BASE_URI = "baseUri";
	private static final String DEFAULT_REPORT_FILE_NAME = "report.csv";

	private String target;
	private String allowedUri;
	private String reportFileName;

	private CliOptions() {
	}

	public static CliOptions fromArgs(String[] args) throws ParseException {
		Options opts = getOptions();

		CommandLineParser parser = new GnuParser();
		CommandLine line;
		try {
			CliOptions result = new CliOptions();

			line = parser.parse(opts, args);

			result.target = decorateUriIfNeeded(line.getOptionValue(TARGET_URI));

			String baseUri;
			if (line.hasOption(BASE_URI)) {
				baseUri = line.getOptionValue(BASE_URI);
			} else {
				baseUri = result.target;
			}
			result.allowedUri = decorateUriIfNeeded(computeAllowedUri(baseUri));

			if (line.hasOption(REPORT_FILE_NAME)) {
				result.reportFileName = line.getOptionValue(REPORT_FILE_NAME);
			} else {
				result.reportFileName = DEFAULT_REPORT_FILE_NAME;
			}

			return result;
		} catch (ParseException e) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("link-checker", opts, true);
			throw e;
		}
	}

	@SuppressWarnings("static-access")
	private static Options getOptions() {
		Option targetUri = OptionBuilder.hasArg().withArgName("URI")
				.withLongOpt(TARGET_URI).isRequired()
				.withDescription("uri to check").create("t");

		Option baseUri = OptionBuilder
				.hasArg()
				.withArgName("URI")
				.withLongOpt(BASE_URI)
				.withDescription(
						"follow only the link below this uri; default: equals to target")
				.create("b");

		Option reportFileName = OptionBuilder
				.hasArg()
				.withArgName("FILE")
				.withLongOpt(REPORT_FILE_NAME)
				.withDescription("save the report to FILE; default: report.csv")
				.create("r");

		Options options = new Options();
		options.addOption(targetUri);
		options.addOption(baseUri);
		options.addOption(reportFileName);
		return options;
	}

	private static String computeAllowedUri(String baseUri) {
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
		return allowed.toString();
	}

	private static String decorateUriIfNeeded(String uri) {
		String result = uri;
		if (!uri.startsWith("http")) {
			result = "http://" + uri;
		}
		return result;
	}

	public String getTarget() {
		return target;
	}

	public String getReportFileName() {
		return reportFileName;
	}

	public String getAllowedUri() {
		return allowedUri;
	}

}
