package info.kupczynski.linkchecker.checker;

import java.util.Collection;
import java.util.Map;

public interface UriService {

	Map<String, UriStatusDTO> start(String uri);

	void finished(UriStatusDTO status);

	void finished(UriStatusDTO status, Collection<String> children);
}
