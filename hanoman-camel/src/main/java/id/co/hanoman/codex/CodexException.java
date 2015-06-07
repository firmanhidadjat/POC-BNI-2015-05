package id.co.hanoman.codex;


public class CodexException extends Exception {
	private static final long serialVersionUID = 370378884049632903L;

	public CodexException() {
		super();
	}

	public CodexException(String msg) {
		super(msg);
	}

	public CodexException(Throwable cause) {
		super(cause);
	}
	
	public CodexException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
