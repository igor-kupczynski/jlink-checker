package kupczynski.info.linkchecker.http;

import java.io.IOException;

public interface UriFetcher {

	HttpResponseWrapper fetch(String uri) throws IOException;
}
