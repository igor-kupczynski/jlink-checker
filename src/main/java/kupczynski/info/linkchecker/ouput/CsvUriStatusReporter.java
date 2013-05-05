package kupczynski.info.linkchecker.ouput;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import kupczynski.info.linkchecker.checker.api.UriStatusDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

public class CsvUriStatusReporter {

	private static final Logger logger = LoggerFactory
			.getLogger(CsvUriStatusReporter.class);

	private final String fname;

	public CsvUriStatusReporter(String fname) {
		this.fname = fname;
	}

	public void reportUris(Collection<UriStatusDTO> uris) {
		ICsvBeanWriter beanWriter = null;
		try {
			beanWriter = new CsvBeanWriter(new FileWriter(fname),
					CsvPreference.STANDARD_PREFERENCE);
			beanWriter.writeHeader(headers);
			for (UriStatusDTO item : uris) {
				beanWriter.write(item, headers, processors);
			}

		} catch (IOException e) {
			throw new RuntimeException(e);
		} finally {
			if (beanWriter != null) {
				try {
					beanWriter.close();
				} catch (IOException e) {
					logger.error("Error closing BeanWriter", e);
				}
			}
		}

	}

	private final CellProcessor[] processors = new CellProcessor[] {
			new NotNull(), // status
			new NotNull(), // message
			new NotNull(), // httpCode
			new NotNull(), // fromUri
			new NotNull(), // baseUri
			new NotNull(), // finalUri
			new NotNull(), // depth
	};

	private final String[] headers = new String[] { "status", "message",
			"httpCode", "fromUri", "baseUri", "finalUri", "depth" };

}
