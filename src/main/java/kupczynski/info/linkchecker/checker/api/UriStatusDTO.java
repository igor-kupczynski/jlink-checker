package kupczynski.info.linkchecker.checker.api;

public class UriStatusDTO {

	public static enum Code {
		OK, WARNING, ERROR
	}

	private final String fromUri;
	private final Integer depth;
	private final String baseUri;
	private final String finalUri;
	private final Code status;
	private final Integer httpCode;
	private final String message;

	public UriStatusDTO(String fromUri, Integer depth, String baseUri,
			String optional, Integer httpCode, String message, Code status) {
		this.fromUri = fromUri;
		this.depth = depth;
		this.baseUri = baseUri;
		this.finalUri = optional;
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

	public String getBaseUri() {
		return baseUri;
	}

	public String getFinalUri() {
		return finalUri;
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
				+ ", baseUri=" + baseUri + ", finalUri=" + finalUri
				+ ", status=" + status + ", httpCode=" + httpCode
				+ ", message=" + message + "]";
	}

}
