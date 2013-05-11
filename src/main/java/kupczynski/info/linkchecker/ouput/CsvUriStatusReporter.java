package kupczynski.info.linkchecker.ouput;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import kupczynski.info.linkchecker.checker.UriStatusDTO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.cellprocessor.constraint.NotNull;
import org.supercsv.cellprocessor.ift.CellProcessor;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;
import org.supercsv.quote.AlwaysQuoteMode;

public class CsvUriStatusReporter {

	private static final Logger logger = LoggerFactory
			.getLogger(CsvUriStatusReporter.class);

	private static final CsvPreference ALWAYS_QUOTE = new CsvPreference.Builder(
			CsvPreference.EXCEL_NORTH_EUROPE_PREFERENCE).useQuoteMode(
			new AlwaysQuoteMode()).build();

	private final String fname;

	public CsvUriStatusReporter(String fname) {
		this.fname = fname;
	}

	public void reportUris(Collection<UriStatusDTO> uris) {
		ICsvBeanWriter beanWriter = null;
		try {
			beanWriter = new CsvBeanWriter(new FileWriter(fname), ALWAYS_QUOTE);
			beanWriter.writeHeader(headers);
			for (UriStatusDTO item : uris) {
				beanWriter.write(item, columns, processors);
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
			new NotNull(), // uri
			new NotNull(), // depth
	};

	private final String[] columns = new String[] { "status", "message",
			"httpCode", "fromUri", "uri", "depth" };

	private final String[] headers = new String[] { "Status", "Message",
			"HTTP Code", "On page", "Link to", "Depth" };

}
