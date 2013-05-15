package info.kupczynski.linkchecker.http.impl;


import info.kupczynski.linkchecker.common.BaseTest;
import info.kupczynski.linkchecker.http.HttpResponseWrapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.protocol.BasicHttpContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.internal.matchers.InstanceOf;

import java.io.IOException;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.when;


public class UriFetcherImplTest extends BaseTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse httpResponse;

    private UriFetcherImpl uriFetcher;

    @Test
    public void assertWrapsHttpResponse() throws IOException {
        // With
        final String uri = "http://example.com/";

        // Do
        HttpResponseWrapper wrapper = uriFetcher.fetch(uri);

        // Assert
        assertThat(wrapper.getUri()).isEqualTo(uri);
        assertThat(wrapper.getContext()).isEqualTo(httpResponse);
    }

    @Before
    public void setUp() {
        super.setUp();
        try {
            when(httpClient.execute((HttpGet) argThat(new InstanceOf(HttpGet.class)),
                    (BasicHttpContext) argThat(new InstanceOf(BasicHttpContext.class)))).thenReturn(httpResponse);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        uriFetcher = new UriFetcherImpl(httpClient);
    }

}
