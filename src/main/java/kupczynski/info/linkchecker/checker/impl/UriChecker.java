package kupczynski.info.linkchecker.checker.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;

import kupczynski.info.linkchecker.checker.api.UriService;
import kupczynski.info.linkchecker.checker.api.UriStatusDTO;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class UriChecker implements Runnable {

	private static final Logger logger = LoggerFactory
			.getLogger(UriChecker.class);

	private final UriService uriService;
	private final boolean follow;
	private final String from;
	private final String uri;
	private final Integer depth;

	public UriChecker(UriService uriService, boolean follow, String from,
			String uri, Integer depth) {
		this.uriService = uriService;
		this.follow = follow;
		this.from = from;
		this.uri = uri;
		this.depth = depth;
	}

	@Override
	public void run() {
		logger.info("Fetching {}", uri);
		UriStatusDTO result = null;
		Collection<String> children = null;

		HttpResponse resp;
		try {
			resp = fetchUri(uri);
			result = fromResponse(resp);

			boolean isHtml = resp.getFirstHeader("Content-type").getValue()
					.startsWith("text/html");
			if (follow && isHtml) {
				children = extractChildren(resp);
			}
		} catch (RuntimeException e) {
			// TODO Log the failure
			result = new UriStatusDTO(from, depth, uri, uri, -1,
					"Runntime Error", UriStatusDTO.Code.ERROR);
		}

		if (children != null) {
			uriService.finished(result, children);
		} else {
			uriService.finished(result);
		}
	}

	private HttpResponse fetchUri(String uri) {
		// TODO: multithreading
		HttpClient client = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(uri);

		HttpResponse response;
		try {
			response = client.execute(httpget);
		} catch (ClientProtocolException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		return response;
		// int code = response.getStatusLine().getStatusCode();
		// if (code == 301 || code == 302) {
		// return fetchUri(response.getFirstHeader("Location").getValue());
		// } else {
		// return response;
		// }
	}

	private UriStatusDTO fromResponse(HttpResponse resp) {
		StatusLine statusLine = resp.getStatusLine();
		return new UriStatusDTO(from, depth, uri, uri,
				statusLine.getStatusCode(), statusLine.getReasonPhrase(),
				statusLine.getStatusCode() == 200 ? UriStatusDTO.Code.OK
						: UriStatusDTO.Code.ERROR);
	}

	private Collection<String> extractChildren(HttpResponse resp) {

		URL url;
		try {
			url = new URL(uri);
		} catch (MalformedURLException e) {
			throw new AssertionError(e);
		}

		String base = url.getProtocol() + "://" + url.getHost();

		// TODO: encoding

		Document document;
		try {
			document = Jsoup
					.parse(resp.getEntity().getContent(), "UTF-8", base);

		} catch (IllegalStateException e) {
			throw new AssertionError(e); // TODO: something better
		} catch (IOException e) {
			throw new AssertionError(e); // TODO: something better
		}

		Builder<String> builder = ImmutableList.<String> builder();

		Elements links = document.select("a[href]");
		for (Element e : links) {
			builder.add(e.absUrl("href"));
		}

		Elements imgs = document.select("img[src]");
		for (Element e : imgs) {
			builder.add(e.absUrl("src"));
		}

		return builder.build();
	}

}
