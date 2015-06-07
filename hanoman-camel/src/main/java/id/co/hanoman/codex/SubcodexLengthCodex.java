package id.co.hanoman.codex;

import id.co.hanoman.codex.GroupCodex.CloseListener;
import id.co.hanoman.codex.GroupCodex.GroupCodexContext;
import id.co.hanoman.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubcodexLengthCodex extends StringCodex implements CloseListener {
	private static final Logger LOG = LoggerFactory.getLogger(SubcodexLengthCodex.class);
	
	protected String subcodex;
	protected String subcodexFullname;

	public SubcodexLengthCodex(CodexFactory factory) {
		super(factory);
		DEFAULT_PADDING_CHAR = "0";
		DEFAULT_PADDING_MODE = "left";
	}
	
	@Override
		public void init(Codex parent, String id, Config config) throws Exception {
			super.init(parent, id, config);
			subcodex = config.getStringValue("@subcodex");
			subcodexFullname = parent != null ? parent.getFullName() + "/" + subcodex : subcodex;
		}
	
	@Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
		((GroupCodexContext) ctx).addCloseListener(this);
		ctx.setParam(getFullName()+":pos", ctx.buffer().writerIndex());
		super.encode(ctx, msg);
	}

	public void close(CodexContext ctx, boolean encode) {
		if (encode) {
			try {
				int p = ctx.buffer().writerIndex();
				ctx.buffer().writerIndex(ctx.getParam(getFullName()+":pos", Integer.class));
				super.encodeValue(ctx, ctx.getParam(subcodexFullname+":length", Integer.class).toString());
				ctx.buffer().writerIndex(p);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		} else {
			LOG.info("SUBCODEX LENGTH "+subcodexFullname+"  "+ctx.getParam(subcodexFullname+":length", Integer.class));
		}
	}

}
