package id.co.hanoman.codex;

import id.co.hanoman.config.Config;

import java.nio.ByteBuffer;

import com.google.gson.JsonObject;

public interface Codex {

	void init(Codex parent, Config config) throws Exception;

	String getName();
	
	String getFullName();
	
	void encode(CodexContext ctx, JsonObject msg) throws Exception;
	
	boolean decode(CodexContext ctx, JsonObject msg, ByteBuffer buf) throws Exception;

}
