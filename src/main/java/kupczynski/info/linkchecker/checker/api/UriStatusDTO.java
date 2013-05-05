package kupczynski.info.linkchecker.checker.api;

public class UriStatusDTO {

	public static enum Code {
		OK, WARNING, ERROR
	}

	private final String fromUri;
	private final Integer depth;
	private final String uri;
	private final Code status;
	private final Integer httpCode;
	private final String message;

	public UriStatusDTO(String fromUri, Integer depth, String uri,
			Integer httpCode, String message, Code status) {
		this.fromUri = fromUri;
		this.depth = depth;
		this.uri = uri;
		this.httpCode = httpCode;
		this.message = message;
		this.status = status;
	}

	public String getFromUri() {
		return fromUri;
	}

	public Integer getDepth() {
		return depth;
	}

	public String getUri() {
		return uri;
	}

	public Code getStatus() {
		return status;
	}

	public Integer getHttpCode() {
		return httpCode;
	}

	public String getMessage() {
		return message;
	}

	@Override
	public String toString() {
		return "UriStatusDTO [fromUri=" + fromUri + ", depth=" + depth
				+ ", uri=" + uri + ", status=" + status + ", httpCode="
				+ httpCode + ", message=" + message + "]";
	}

}
