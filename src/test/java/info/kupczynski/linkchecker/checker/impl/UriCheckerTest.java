package info.kupczynski.linkchecker.checker.impl;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Collection;

import info.kupczynski.linkchecker.checker.UriService;
import info.kupczynski.linkchecker.checker.UriStatusDTO;
import info.kupczynski.linkchecker.http.HttpResponseWrapper;
import info.kupczynski.linkchecker.http.UriFetcher;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.google.common.collect.Lists;

public class UriCheckerTest {

	private static final String from = "http://example.com/";
	private static final String to = "http://example.com/other";
	private static final int depth = 3;
	UriService uriService;

	@Before
	public void setUp() {
		uriService = mockUriService();
	}

	@Test
	public void assertCallsBackUriService() {
		// With
		UriChecker uriChecker = new UriChecker(uriService, true, from, to,
				depth);
		UriChecker.setUriFetcher(mockUriFetcher(mockSimplePage()));

		ArgumentCaptor<UriStatusDTO> uriStatusDTOCaptor = ArgumentCaptor
				.forClass(UriStatusDTO.class);

		// Do
		uriChecker.run();

		// Assert
		verify(uriService).finished(uriStatusDTOCaptor.capture());

		UriStatusDTO status = uriStatusDTOCaptor.getValue();
		assertThat(status.getHttpCode()).isEqualTo(Integer.valueOf(200));
		assertThat(status.getStatus()).isEqualTo(UriStatusDTO.Code.OK);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void assertFollowOnRedirect() {
		// With
		UriChecker uriChecker = new UriChecker(uriService, true, from, to,
				depth);
		String redirectTo = "http://example.com/foo";
		UriChecker.setUriFetcher(mockUriFetcher(mockRedirect(redirectTo)));

		ArgumentCaptor<UriStatusDTO> uriStatusDTOCaptor = ArgumentCaptor
				.forClass(UriStatusDTO.class);
		ArgumentCaptor<Collection> childrenCaptor = ArgumentCaptor
				.forClass(Collection.class);

		// Do
		uriChecker.run();

		// Assert
		verify(uriService).finished(uriStatusDTOCaptor.capture(),
				childrenCaptor.capture());

		UriStatusDTO status = uriStatusDTOCaptor.getValue();
		assertThat(status.getHttpCode()).isEqualTo(Integer.valueOf(301));
		assertThat(status.getStatus()).isEqualTo(UriStatusDTO.Code.WARNING);

		Collection<String> children = childrenCaptor.getValue();
		assertThat(children).hasSize(1).contains(redirectTo);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void assertFollowOnChildren() {
		// With
		UriChecker uriChecker = new UriChecker(uriService, true, from, to,
				depth);
		String item1 = "http://example.com/foo";
		String item2 = "http://example.com/bar";
		UriChecker
				.setUriFetcher(mockUriFetcher(mockWithChildren(item1, item2)));

		ArgumentCaptor<UriStatusDTO> uriStatusDTOCaptor = ArgumentCaptor
				.forClass(UriStatusDTO.class);
		ArgumentCaptor<Collection> childrenCaptor = ArgumentCaptor
				.forClass(Collection.class);

		// Do
		uriChecker.run();

		// Assert
		verify(uriService).finished(uriStatusDTOCaptor.capture(),
				childrenCaptor.capture());

		UriStatusDTO status = uriStatusDTOCaptor.getValue();
		assertThat(status.getHttpCode()).isEqualTo(Integer.valueOf(200));
		assertThat(status.getStatus()).isEqualTo(UriStatusDTO.Code.OK);

		Collection<String> children = childrenCaptor.getValue();
		assertThat(children).hasSize(2).contains(item1).contains(item2);
	}

	private UriService mockUriService() {
		UriService us = mock(UriService.class);
		return us;
	}

	private UriFetcher mockUriFetcher(HttpResponseWrapper wrapper) {
		UriFetcher uf = mock(UriFetcher.class);
		try {
			when(uf.fetch(to)).thenReturn(wrapper);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return uf;
	}

	private HttpResponseWrapper mockSimplePage() {
		return mockHttpResponseWrapper(200, false,
				Lists.<String> newArrayList());
	}

	private HttpResponseWrapper mockRedirect(String to) {
		return mockHttpResponseWrapper(301, true, Lists.newArrayList(to));
	}

	private HttpResponseWrapper mockWithChildren(String... children) {
		return mockHttpResponseWrapper(200, true, Lists.newArrayList(children));
	}

	private HttpResponseWrapper mockHttpResponseWrapper(int code,
			boolean isRedirect, Collection<String> nextLinks) {
		HttpResponseWrapper wrapper = mock(HttpResponseWrapper.class);
		when(wrapper.getHttpCode()).thenReturn(code);
		when(wrapper.isRedirect()).thenReturn(isRedirect);
		when(wrapper.nextLinks()).thenReturn(nextLinks);
		return wrapper;
	}

}
