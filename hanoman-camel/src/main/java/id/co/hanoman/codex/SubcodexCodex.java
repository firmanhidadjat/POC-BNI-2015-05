package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SubcodexCodex extends BaseCodex {
	private static final Logger LOG = LoggerFactory.getLogger(SubcodexCodex.class);
	
	String field;
	String fieldEval;
	String prefix;

	public SubcodexCodex(CodexFactory factory) {
		super(factory);
	}
	
	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		this.field = config.getStringValue("@field");
		this.fieldEval = config.getStringValue("@field-eval");
		this.prefix = config.getStringValue("@prefix", "");
		super.init(parent, id, config);
	}

	@Override
	public Object decode(CodexContext ctx, Object msg) throws Exception {
		if (fieldEval != null) {
			Codex subcodex = factory.getCodex(ctx.eval(msg, fieldEval));
			Object submsg = ctx.valueHandler.getCodexValue(ctx, msg, getName(), Object.class);
			if (submsg == null) submsg = ctx.valueHandler.newObject(subcodex);
			if (LOG.isTraceEnabled()) LOG.trace("SubCodex '"+subcodex+"' "+U.dump(submsg));
			int p0 = ctx.buffer().readerIndex();
			submsg = subcodex.decode(ctx, submsg);
			int p1 = ctx.buffer().readerIndex();
			ctx.setParam(getFullName()+":length", p1-p0);
			ctx.valueHandler.setCodexValue(ctx, msg, getName(), submsg);
			if (LOG.isTraceEnabled()) LOG.trace("SubCodex '"+subcodex+"' RESULT "+U.dump(submsg));
			return submsg;
		} else {
			Codex subcodex = factory.getCodex(prefix+ctx.valueHandler.getCodexValue(ctx, msg, field, String.class));
			Object submsg = ctx.valueHandler.getCodexValue(ctx, msg, getName(), Object.class);
			if (submsg == null) submsg = ctx.valueHandler.newObject(subcodex);
			if (LOG.isTraceEnabled()) LOG.trace("SubCodex '"+subcodex+"' "+U.dump(submsg));
			int p0 = ctx.buffer().readerIndex();
			submsg = subcodex.decode(ctx, submsg);
			int p1 = ctx.buffer().readerIndex();
			ctx.setParam(getFullName()+":length", p1-p0);
			ctx.valueHandler.setCodexValue(ctx, msg, getName(), submsg);
			if (LOG.isTraceEnabled()) LOG.trace("SubCodex '"+subcodex+"' RESULT "+U.dump(submsg));
			return submsg;
		}
	}

	@Override
	public void encode(CodexContext ctx, Object msg) throws Exception {
		if (fieldEval != null) {
			Codex subcodex = factory.getCodex(ctx.eval(msg, fieldEval));
			Object submsg = ctx.valueHandler.getCodexValue(ctx, msg, getName(), Object.class);
			if (LOG.isTraceEnabled()) LOG.trace("SUBCODEX ["+getFullName()+"]  "+U.dump(submsg));
			int p0 = ctx.buffer().writerIndex();
			subcodex.encode(ctx, submsg);
			int p1 = ctx.buffer().writerIndex();
			ctx.setParam(getFullName()+":length", p1-p0);
			if (LOG.isTraceEnabled()) LOG.trace("SUBCODEX ["+getFullName()+":length]  "+(p1-p0));
		} else {
			Codex subcodex = factory.getCodex(prefix+ctx.valueHandler.getCodexValue(ctx, msg, field, String.class));
			if (LOG.isDebugEnabled()) LOG.debug("SubCodex "+U.dump(subcodex));
			Object submsg = ctx.valueHandler.getCodexValue(ctx, msg, getName(), Object.class);
			if (LOG.isTraceEnabled()) LOG.trace("SUBCODEX ["+getFullName()+"]  "+U.dump(submsg));
			int p0 = ctx.buffer().writerIndex();
			subcodex.encode(ctx, submsg);
			int p1 = ctx.buffer().writerIndex();
			ctx.setParam(getFullName()+":length", p1-p0);
			if (LOG.isTraceEnabled()) LOG.trace("SUBCODEX ["+getFullName()+":length]  "+(p1-p0));
		}
	}

}
