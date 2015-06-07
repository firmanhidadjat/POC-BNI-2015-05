package id.co.hanoman.codex;

public interface GroupLength extends Codex {

	public void encodeLength(CodexContext ctx, int length) throws Exception;
	
}
