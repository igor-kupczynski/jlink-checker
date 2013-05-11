package kupczynski.info.linkchecker.http;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;

import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class HttpResponseWrapper implements Closeable {

	private static final String TEXT_HTML = "text/html";
	private static final String CONTENT_TYPE = "Content-Type";
	private static final String LOCATION = "Location";

	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String CHARSET_PREFIX = "charset=";

	private static final int[] REDIRECT = new int[] { 301, 302 };

	private final String uri;
	private final HttpResponse context;

	public HttpResponseWrapper(String uri, HttpResponse context) {
		this.uri = checkNotNull(uri);
		this.context = checkNotNull(context);
	}

	public boolean isHtml() {
		return context.getFirstHeader(CONTENT_TYPE).getValue()
				.startsWith(TEXT_HTML);
	}

	public boolean isRedirect() {
		int code = context.getStatusLine().getStatusCode();
		for (int item : REDIRECT) {
			if (item == code)
				return true;
		}
		return false;
	}

	public Collection<String> nextLinks() {
		Collection<String> result = null;

		if (isRedirect()) {
			result = nextFromRedirect();
		} else {
			result = nextFromHtml();
		}
		return result;
	}

	public int getHttpCode() {
		return context.getStatusLine().getStatusCode();
	}

	public String getHttpReason() {
		return context.getStatusLine().getReasonPhrase();
	}

	@Override
	public void close() throws IOException {
		EntityUtils.consume(context.getEntity());
	}

	private Collection<String> nextFromRedirect() {
		return ImmutableList.of(context.getFirstHeader(LOCATION).getValue());
	}

	private Collection<String> nextFromHtml() {
		Document document;
		try {
			document = Jsoup.parse(context.getEntity().getContent(),
					getEncoding(), uri);

		} catch (IllegalStateException e) {
			throw new AssertionError(e);
		} catch (IOException e) {
			throw new AssertionError(e);
		}

		Builder<String> builder = ImmutableList.<String> builder();

		if (document != null) {
			Elements links = document.select("a[href]");
			for (Element e : links) {
				builder.add(e.absUrl("href"));
			}

			Elements imgs = document.select("img[src]");
			for (Element e : imgs) {
				builder.add(e.absUrl("src"));
			}
		}

		return builder.build();
	}

	private String getEncoding() {
		String result = DEFAULT_ENCODING;
		String ct = context.getFirstHeader(CONTENT_TYPE).getValue();

		String[] items = ct.split(";");
		if (items.length == 2) {
			String enc = items[1];
			int idx = enc.indexOf(CHARSET_PREFIX);
			if (idx >= 0) {
				result = enc.substring(idx + CHARSET_PREFIX.length()).trim()
						.toUpperCase();
			}
		}

		return result;
	}
}
