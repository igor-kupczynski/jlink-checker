package kupczynski.info.linkchecker;

import java.util.Map;

import kupczynski.info.linkchecker.checker.api.UriService;
import kupczynski.info.linkchecker.checker.api.UriServiceFactory;
import kupczynski.info.linkchecker.checker.api.UriStatusDTO;
import kupczynski.info.linkchecker.checker.impl.DefaulUriServiceFactory;
import kupczynski.info.linkchecker.ouput.CsvUriStatusReporter;

public class Runner {

	public static final String base_uri = "http://masz-prawo.info/";

	public static void main(String[] args) {

		UriServiceFactory factory = new DefaulUriServiceFactory(base_uri);

		UriService uri = factory.createUriService();
		Map<String, UriStatusDTO> result = uri.start("http://masz-prawo.info/");

		CsvUriStatusReporter reporter = new CsvUriStatusReporter("report.csv");
		reporter.reportUris(result.values());
	}
}
