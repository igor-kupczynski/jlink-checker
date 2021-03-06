package info.kupczynski.linkchecker.checker.impl;

import info.kupczynski.linkchecker.checker.UriService;
import info.kupczynski.linkchecker.checker.UriStatusDTO;
import info.kupczynski.linkchecker.http.HttpResponseWrapper;
import info.kupczynski.linkchecker.http.UriFetcher;
import info.kupczynski.linkchecker.http.impl.UriFetcherImpl;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Collection;

public class UriChecker implements Runnable {

    private static final Logger logger = LoggerFactory
            .getLogger(UriChecker.class);

    private final UriService uriService;
    private final boolean follow;
    private final String from;
    private final String uri;
    private final Integer depth;

    private final static PoolingClientConnectionManager CONNECTION_MANAGER = new PoolingClientConnectionManager();
    private final static HttpClient CLIENT = new DefaultHttpClient(
            CONNECTION_MANAGER);

    static {
        CONNECTION_MANAGER.setMaxTotal(16);
        CONNECTION_MANAGER.setDefaultMaxPerRoute(8);
    }

    private static UriFetcher uriFetcher = new UriFetcherImpl(CLIENT);

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
        UriStatusDTO result;
        Collection<String> children = null;

        HttpResponseWrapper resp = null;
        try {
            resp = uriFetcher.fetch(uri);
            result = fromResponse(resp);

            if (follow && (resp.isHtml() || resp.isRedirect())) {
                children = resp.nextLinks();
            }
        } catch (IOException e) {
            logger.warn("Error fetching {}", uri);
            result = new UriStatusDTO(from, depth, uri, -1,
                    "RuntimeException: " + e.getMessage(),
                    UriStatusDTO.Code.ERROR);
        }

        if (children != null) {
            uriService.finished(result, children);
        } else {
            uriService.finished(result);
        }
        if (resp != null) {
            try {

                resp.close();

            } catch (IOException e) {
                logger.warn("Error closing", e);
            }
        }
    }

    private UriStatusDTO fromResponse(HttpResponseWrapper resp) {
        return new UriStatusDTO(from, depth, uri, resp.getHttpCode(),
                resp.getHttpReason(), getStatusCode(resp));
    }

    private UriStatusDTO.Code getStatusCode(HttpResponseWrapper resp) {

        UriStatusDTO.Code result;

        switch (resp.getHttpCode()) {
            case 200:
                result = UriStatusDTO.Code.OK;
                break;
            case 301:
            case 302:
                result = UriStatusDTO.Code.WARNING;
                break;
            default:
                result = UriStatusDTO.Code.ERROR;
                break;
        }

        return result;
    }

    /**
     * For tests only
     *
     * @param uriFetcher UriFetcher implementation to be set
     */
    static void setUriFetcher(UriFetcher uriFetcher) {
        UriChecker.uriFetcher = uriFetcher;
    }

}
