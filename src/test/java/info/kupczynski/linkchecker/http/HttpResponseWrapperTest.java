package info.kupczynski.linkchecker.http;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicHttpResponse;
import org.junit.Test;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

public class HttpResponseWrapperTest {

    private static final String REDIRECT_URI = "http://example.com/foo/";
    private static final String URI = "http://example.com/";

    private static final String LINK_BAR = "http://example.com/bar/";
    private static final String LINK_FOO = "http://example.com/foo/";

    private static final String HTML = "<html><body><a href=\"" + LINK_FOO + "\">foo</a><a href=\"" + LINK_BAR +
            "\">bar</a></body></html>";

    @Test
    public void assertContextGetters() {
        // With
        HttpResponse mockResponse = mock(HttpResponse.class);

        // Do
        HttpResponseWrapper wrapper = new HttpResponseWrapper(URI, mockResponse);

        // Assert
        assertThat(wrapper.getUri()).isEqualTo(URI);
        assertThat(wrapper.getContext()).isEqualTo(mockResponse);
        verifyZeroInteractions(mockResponse);
    }

    @Test
    public void assertSimpleAttributes() {
        // With
        HttpResponse resp = createHtmlPageResponse();

        // Do
        HttpResponseWrapper wrapper = new HttpResponseWrapper(URI, resp);

        // Assert
        assertThat(wrapper.getHttpCode()).isEqualTo(200);
        assertThat(wrapper.getHttpReason()).isEqualTo("OK");
    }

    @Test
    public void assertIsHtml() {
        // With
        HttpResponse htmlResp = createHtmlPageResponse();
        HttpResponse jsonResp = createJsonPageResponse();

        // Do
        HttpResponseWrapper htmlRespWrapper = new HttpResponseWrapper(URI, htmlResp);
        HttpResponseWrapper jsonRespWrapper = new HttpResponseWrapper(URI, jsonResp);

        // Assert
        assertThat(htmlRespWrapper.isHtml()).isTrue();
        assertThat(jsonRespWrapper.isHtml()).isFalse();
    }

    @Test
    public void assertNextLinksOnRedirect() {
        // With
        HttpResponse resp = createRedirectResponse();

        // Do
        HttpResponseWrapper wrapper = new HttpResponseWrapper(URI, resp);

        // Assert
        assertThat(wrapper.nextLinks()).hasSize(1).contains(REDIRECT_URI);
    }

    @Test
    public void assertNextLinksOnHtml() {
        // With
        HttpResponse resp = createHtmlPageResponse();

        // Do
        HttpResponseWrapper wrapper = new HttpResponseWrapper(URI, resp);

        // Assert
        assertThat(wrapper.nextLinks()).hasSize(2).contains(LINK_FOO).contains(LINK_BAR);
    }

    @Test
    public void assertNextLinksErrorOnJson() {
        // With
        HttpResponse resp = createJsonPageResponse();

        // Do
        HttpResponseWrapper wrapper = new HttpResponseWrapper(URI, resp);

        // Assert
        try {
            wrapper.nextLinks();
            failBecauseExceptionWasNotThrown(IllegalArgumentException.class);
        } catch (IllegalArgumentException e) {
            // Expected
        }

    }


    @Test
    public void assertIsRedirect() {
        // With
        HttpResponse htmlResp = createHtmlPageResponse();
        HttpResponse redirectResp = createRedirectResponse();

        // Do
        HttpResponseWrapper htmlRespWrapper = new HttpResponseWrapper(URI, htmlResp);
        HttpResponseWrapper redirectRespWrapper = new HttpResponseWrapper(URI, redirectResp);

        // Assert
        assertThat(htmlRespWrapper.isRedirect()).isFalse();
        assertThat(redirectRespWrapper.isRedirect()).isTrue();
    }

    public HttpResponse createHtmlPageResponse() {
        HttpResponse message = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        message.setHeader(new BasicHeader("Content-Type", "text/html"));

        HttpEntity entity = new StringEntity(HTML, ContentType.TEXT_HTML);
        message.setEntity(entity);

        return message;
    }

    public HttpResponse createRedirectResponse() {
        HttpResponse message = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 301, "Moved Permanently");
        message.setHeader(new BasicHeader("Location", REDIRECT_URI));

        return message;
    }

    public HttpResponse createJsonPageResponse() {
        HttpResponse message = new BasicHttpResponse(new ProtocolVersion("HTTP", 1, 1), 200, "OK");
        message.setHeader(new BasicHeader("Content-Type", "application/json"));

        return message;
    }
}
