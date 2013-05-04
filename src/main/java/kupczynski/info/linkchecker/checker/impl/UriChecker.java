package kupczynski.info.linkchecker.checker.impl;

import java.io.IOException;

import javax.management.RuntimeErrorException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import kupczynski.info.linkchecker.checker.api.UriService;
import kupczynski.info.linkchecker.checker.api.UriStatusDTO;

public class UriChecker implements Runnable {

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

	public UriStatusDTO check() {

		UriStatusDTO result;

		try {
			HttpResponse resp = fetchUri(uri);
			result = fromResponse(resp);
		} catch (RuntimeException e) {
			// TODO Log the failure
			result = new UriStatusDTO(from, depth, uri, uri, -1,
					"Runntime Error", UriStatusDTO.Code.ERROR);
		}

		// TODO: maybe parse children
		
		return result;
	}

	@Override
	public void run() {
		UriStatusDTO status = check();
		uriService.finished(status);
	}

	private HttpResponse fetchUri(String uri) {
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
}
