package kupczynski.info.linkchecker.checker.http;

import java.io.IOException;

public interface UriFetcher {

	HttpResponseWrapper fetch(String uri) throws IOException;
}
