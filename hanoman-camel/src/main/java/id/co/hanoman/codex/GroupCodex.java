package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.jboss.netty.buffer.ChannelBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GroupCodex extends BaseCodex {
	private static final Logger LOG = LoggerFactory.getLogger(GroupCodex.class);
	protected boolean allowPartial;
	protected Codex codex[];

	public GroupCodex(CodexFactory factory) {
		super(factory);
	}

	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		super.init(parent, id, config);
		allowPartial = config.getBooleanValue("@allow-partial", false);
		List<Config> lc = config.getList("field");
		codex = new Codex[lc.size()];
		Iterator<Config> lci = lc.iterator();
		for (int i = 0; lci.hasNext(); i++) {
			Config e = lci.next();
			String type = e.getStringValue("@type");
			codex[i] = factory.getCodexByType(this, type, e);
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT [" + getFullName() + "] allowPartial = [" + allowPartial + "]");
		}
	}

	@Override
	public Object decode(CodexContext pctx, Object msg) throws Exception {
		ChannelBuffer buf = pctx.buffer();
		if (LOG.isTraceEnabled())
			LOG.trace("DECODE [" + getFullName() + "]");
		int p = buf.readerIndex();
		GroupCodexContext ctx = new GroupCodexContext(pctx, pctx.buffer());
		pctx.setParam(getFullName(), ctx);
		try {
			if (LOG.isTraceEnabled()) {
				LOG.trace("DECODE [" + getFullName() + "] STEP 1");
			}
			int blt = buf.writerIndex();
			int bp = buf.readerIndex();
			try {
				if (msg == null)
					msg = ctx.getValueHandler().newObject(this);
				int length = -1;
				for (int i = 0, il = codex.length; i < il; i++) {
					Codex c = codex[i];
					try {
						if (LOG.isTraceEnabled()) {
							LOG.trace("DECODE " + (i + 1) + " [" + c.getFullName() + "] @" + buf.readerIndex());
						}
						if (c.decode(ctx, msg) == null) {
							LOG.trace("DEOCDE " + (i + 1) + " [" + c.getFullName() + "] STOP @" + buf.readerIndex());
							if (!allowPartial || length == -1) {
								buf.readerIndex(p);
								return null;
							} else {
								throw new Exception("[" + c.getFullName() + "] INCOMPLETE\nDATA [" + U.dump(buf.array(), buf.readerIndex(), buf.readableBytes()) + "]");
							}
						}
						if (c instanceof GroupLength) {
							length = ctx.getValueHandler().getCodexValue(ctx, msg, c.getName(), Integer.class);
							if (LOG.isTraceEnabled()) LOG.trace("CHECK LENGTH "+blt+"  "+bp+" + "+length+" = "+(bp+length));
							if (blt < bp+length) return null;
							buf.writerIndex(bp+length);
							
						}
					} catch (Exception ex) {
						throw new Exception("[" + c.getFullName() + "] " + ex.getMessage() + "\nDATA [" + U.dump(buf.array(), buf.readerIndex(), buf.readableBytes()) + "]", ex);
					}
				}
				ctx.getValueHandler().setCodexValue(ctx, msg, "@codex", fid);
				if (LOG.isTraceEnabled()) {
					LOG.trace("DECODE [" + getFullName() + "]  " + U.dump(msg) + "  " + buf.readerIndex() + "  " + buf.writerIndex() + "  " + buf.capacity() + "  " + blt);
				}
			} finally {
				buf.writerIndex(blt);
			}
			if (pctx.parent == null) {
				ctx.getValueHandler().setCodexValue(ctx, msg, "@raw", U.encode64(buf.array(), p, buf.readerIndex() - p));
			}
		} finally {
			ctx.close(false);
		}
		return msg;
	}

	@Override
	public void encode(CodexContext pctx, Object msg) throws Exception {
		try {
			if (LOG.isTraceEnabled())
				LOG.trace("ENCODE [" + getFullName() + "] @" + pctx.buffer().writerIndex());
			GroupCodexContext ctx = new GroupCodexContext(pctx);
			try {
				if (LOG.isTraceEnabled()) {
					LOG.trace("GROUP [" + getFullName() + "] START " + ctx.buffer().writerIndex());
					int p0 = ctx.buf.writerIndex();
					for (int i = 0, il = codex.length; i < il; i++) {
						Codex c = codex[i];
						int p = ctx.buffer().writerIndex();
						LOG.trace("ENCODE " + (i + 1) + " " + c.getFullName());
						if (c instanceof GroupLength) {
							ctx.setParam(codex[i].getFullName() + ":pos", p);
						}
						c.encode(ctx, msg);
						int p2 = ctx.buffer().writerIndex();
						LOG.trace("ENCODE " + (i + 1) + " [" + codex[i].getFullName() + "]  @" + p + "-" + p2 + "  [" + U.dump(ctx.buffer().array(), p, p2 - p) + "]");
					}
					int p1 = ctx.buf.writerIndex();
					int length = p1 - p0;
					for (int i = 0, il = codex.length; i < il; i++) {
						Codex c = codex[i];
						if (c instanceof GroupLength) {
							int pos = ctx.getParam(codex[i].getFullName() + ":pos", Integer.class);
							ctx.buf.writerIndex(pos);
							((GroupLength) c).encodeLength(ctx, length);
						}
					}
					ctx.buf.writerIndex(p1);
					LOG.trace("GROUP [" + getFullName() + "] END " + ctx.buffer().writerIndex());
				} else {
					int p0 = ctx.buf.writerIndex();
					for (int i = 0, il = codex.length; i < il; i++) {
						Codex c = codex[i];
						if (c instanceof GroupLength) {
							ctx.setParam(codex[i].getFullName() + ":pos", ctx.buffer().writerIndex());
						}
						c.encode(ctx, msg);
					}
					int p1 = ctx.buf.writerIndex();
					int length = p1 - p0;
					for (int i = 0, il = codex.length; i < il; i++) {
						Codex c = codex[i];
						if (c instanceof GroupLength) {
							int pos = ctx.getParam(codex[i].getFullName() + ":pos", Integer.class);
							ctx.buf.writerIndex(pos);
							((GroupLength) c).encodeLength(ctx, length);
						}
					}
					ctx.buf.writerIndex(p1);
				}
			} finally {
				ctx.close(true);
			}
		} catch (CodexException e) {
			throw new CodexException("Codex error " + getFullName() + ": " + e.getMessage(), e);
		}
	}

	public class SubCodexContext extends CodexContext {
		public SubCodexContext(CodexContext parent) {
			super(parent);
		}
	}

	public interface CloseListener {

		void close(CodexContext ctx, boolean encode);

	}

	public class GroupCodexContext extends CodexContext {
		List<CloseListener> closeListeners = new LinkedList<>();

		public GroupCodexContext(CodexContext parent) {
			super(parent);
		}

		public GroupCodexContext(CodexContext parent, ChannelBuffer buf) {
			super(parent, buf);
		}

		public void addCloseListener(CloseListener listener) {
			closeListeners.add(listener);
		}

		@Override
		public void close(boolean encode) throws Exception {
			for (CloseListener cl : closeListeners)
				cl.close(this, encode);
			if (encode) {
				if (LOG.isTraceEnabled())
					LOG.trace("CLOSE ENCODE [" + getFullName() + "] " + buffer().writerIndex());
				parent.buffer().writeBytes(buf);
			}
			super.close(encode);
		}

	}
}
