package id.co.hanoman.codex;

import id.co.hanoman.config.Config;

public interface Codex {

	void init(Codex parent, String id, Config config) throws Exception;

	String getName();
	
	String getFullName();
	
	void encode(CodexContext ctx, Object msg) throws Exception;
	
	Object decode(CodexContext ctx, Object msg) throws Exception;

}
