package kupczynski.info.linkchecker.http.impl;

import java.io.IOException;

import kupczynski.info.linkchecker.http.HttpResponseWrapper;
import kupczynski.info.linkchecker.http.UriFetcher;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UriFetcherImpl implements UriFetcher {

	private static final Logger logger = LoggerFactory
			.getLogger(UriFetcherImpl.class);

	private final HttpClient httpClient;

	public UriFetcherImpl(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	@Override
	public HttpResponseWrapper fetch(String uri) throws IOException {
		logger.info("Fetching {}", uri);
		HttpGet httpget = new HttpGet(uri);

		HttpParams params = httpget.getParams();
		params.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
		httpget.setParams(params);

		HttpResponse response = httpClient.execute(httpget,
				new BasicHttpContext());

		return new HttpResponseWrapper(uri, response);

	}
}
