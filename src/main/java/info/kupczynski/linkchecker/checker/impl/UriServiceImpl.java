package info.kupczynski.linkchecker.checker.impl;

import static com.google.common.base.Preconditions.checkState;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.kupczynski.linkchecker.checker.CutOffStrategy;
import info.kupczynski.linkchecker.checker.UriService;
import info.kupczynski.linkchecker.checker.UriStatusDTO;

import com.google.common.collect.ImmutableMap;

public class UriServiceImpl implements UriService {

	private static final Logger logger = LoggerFactory
			.getLogger(UriServiceImpl.class);

	private boolean started = false;
	private final CountDownLatch isDone = new CountDownLatch(1);
	private final AtomicLong requests = new AtomicLong();

	private final Set<String> seenUris = new HashSet<>();
	private final Map<String, UriStatusDTO> statuses = new ConcurrentHashMap<>();

	private final ExecutorService exec;
	private CutOffStrategy cutOffStrategy;

	public UriServiceImpl(ExecutorService exec, CutOffStrategy cutOffStrategy) {
		this.exec = exec;
		this.cutOffStrategy = cutOffStrategy;
	}

	@Override
	public Map<String, UriStatusDTO> start(String uri) {
		checkState(!started, "This UriSerive was already started.");
		started = true;

		logger.info("Starting {}", uri);

		checkNext(true, "", uri, 0);

		try {
			isDone.await();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} finally {
			exec.shutdown();
		}

		return ImmutableMap.copyOf(statuses);
	}

	@Override
	public void finished(UriStatusDTO status) {
		statuses.put(status.getUri(), status);

		long todo = requests.decrementAndGet();
		if (todo == 0) {
			isDone.countDown();
		}
	}

	@Override
	public void finished(UriStatusDTO status, Collection<String> children) {
		Integer depth = status.getDepth() + 1;

		for (String child : children) {
			synchronized (seenUris) {
				if (!seenUris.contains(child)) {
					seenUris.add(child);
					checkNext(cutOffStrategy.shouldFollow(depth, child),
							status.getUri(), child, depth);
				}
			}
		}

		finished(status);
	}

	private void checkNext(boolean follow, String parent, String uri,
			Integer depth) {
		requests.incrementAndGet();
		exec.submit(new UriChecker(this, follow, parent, uri, depth));
	}

}
