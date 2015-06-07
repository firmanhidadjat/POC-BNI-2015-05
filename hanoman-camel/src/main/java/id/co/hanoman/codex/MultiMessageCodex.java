package id.co.hanoman.codex;

import java.util.LinkedList;
import java.util.List;

import id.co.hanoman.config.Config;

public class MultiMessageCodex extends BaseCodex {
	Codex codex;
	String scriptEngine;

	public MultiMessageCodex(CodexFactory factory) {
		super(factory);
	}
	
	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		super.init(parent, id, config);
		codex = factory.getCodexByType(parent, config.getStringValue("@codex"), config);
		scriptEngine = config.getStringValue("@script-engine", "JavaScript");
	}

	@Override
	public Object decode(CodexContext ctx, Object msg) throws Exception {
		@SuppressWarnings("unchecked")
		List<Object> msgs = ctx.getParam(getFullName()+":msgs", List.class);
		Object obj;
		do {
			obj = codex.decode(ctx, null);
			if (obj != null) {
				if (msgs == null) ctx.setParam(getFullName(), msgs = new LinkedList<>());
				msgs.add(obj);
				
			}
		} while (obj != null);
		return null;
	}

	@Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
		// TODO Auto-generated method stub
		
	}

}
