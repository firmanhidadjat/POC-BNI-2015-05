package id.co.hanoman.codex;

public interface ValueHandler {
	
	void setCodexValue(CodexContext ctx, Object obj, String field, Object value);
	
	void setCodexError(CodexContext ctx, Object obj, String field, String message);
	
	boolean hasCodexValue(CodexContext ctx, Object obj, String field);

	<T> T getCodexValue(CodexContext ctx, Object obj, String field, Class<T> type);
	
	Object newObject(Codex codex);
}
