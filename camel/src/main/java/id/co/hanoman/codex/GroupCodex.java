package id.co.hanoman.codex;

import id.co.hanoman.U;
import id.co.hanoman.config.Config;

import java.nio.ByteBuffer;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

public class GroupCodex extends BaseCodex {
	private static final Logger LOG = LoggerFactory.getLogger(GroupCodex.class);
	protected boolean allowPartial;
	protected Codex codex[];
	protected String cid;

	public void init(Codex parent, Config config) throws Exception {
		super.init(parent, config);
		allowPartial = config.getBooleanValue("@allow-partial", false);
		List<Config> lc = config.getList("field");
		cid = config.getStringValue("@id");
		codex = new Codex[lc.size()];
		Iterator<Config> lci = lc.iterator();
		for (int i = 0; lci.hasNext(); i++) {
			Config e = lci.next();
			String type = e.getStringValue("@type");
			String cn = type.indexOf(".") == -1 ? "id.co.hanoman.codex."
					+ type.substring(0, 1).toUpperCase() + type.substring(1)
					+ "Codex" : type;
			codex[i] = (Codex) Class.forName(cn).newInstance();
			codex[i].init(this, e);
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT [" + getFullName() + "] allowPartial = ["
					+ allowPartial + "]");
		}
	}

	@Override
	public boolean decode(CodexContext pctx, JsonObject msg, ByteBuffer buf)
			throws Exception {
		if (LOG.isDebugEnabled())
			LOG.debug("DECODE [" + getFullName() + "]");
		int p = buf.position();
		GroupCodexContext ctx = new GroupCodexContext(pctx);
		try {
			int gl = 0;
			if (LOG.isTraceEnabled()) {
				LOG.trace("DECODE [" + getFullName() + "] STEP 1");
			}
			int blt = 0;
			try {
				for (int i = 0, il = codex.length; i < il; i++) {
					Codex c = codex[i];
					try {
						if (LOG.isTraceEnabled()) {
							LOG.trace("DECODE [" + c.getFullName()
									+ "] FIELD [" + c.getFullName() + "]");
						}
						if (!c.decode(ctx, msg, buf)) {
							if (!allowPartial || ctx.getLength() == 0) {
								buf.position(p);
								return false;
							}
						}
						if (ctx.getLength() > 0) {
							gl = ctx.lengthPos + ctx.getLength();
							LOG.info("CHECK LIMIT "+buf.limit()+"  "+gl);
							if (buf.limit() < gl) {
								buf.position(p);
								return false;
							} else {
								if (LOG.isDebugEnabled())
									LOG.debug("BUF LIMIT [" + cid + "] " + gl
											+ "  " + buf.limit());
								blt = buf.limit();
								buf.limit(gl);
							}
						}
						if (gl > 0 && buf.position() > gl)
							throw new Exception("Length overlimit "
									+ buf.position() + " -- " + gl);
					} catch (Exception ex) {
						throw new Exception("["
								+ c.getFullName()
								+ "] "
								+ ex.getMessage()
								+ "\nDATA ["
								+ U.dump(buf.array(), buf.position(),
										buf.remaining()) + "]", ex);
					}
				}
				if (msg.has("@codex")) {
					if (msg.get("@codex").isJsonArray()) {
						msg.getAsJsonArray("@codex").add(new JsonPrimitive(fid));
					} else {
						JsonArray arr = new JsonArray();
						arr.add(msg.get("@codex"));
						arr.add(new JsonPrimitive(fid));
						msg.add("@codex", arr);
					}
				} else {
					msg.addProperty("@codex", fid);
				}
			} finally {
				if (blt > 0)
					buf.limit(blt);
			}
			if (pctx.parent == null && !msg.has("@raw")) {
				msg.addProperty("@raw", U.encode64(buf.array(), p, buf.position() - p));
			}
			msg.addProperty("@length", ctx.getLength());
		} finally {
			ctx.close();
		}
		LOG.info("CTX GROUP "+U.dump(ctx));
		LOG.info("PCTX GROUP "+U.dump(pctx));
		return true;
	}

	@Override
	public void encode(CodexContext pctx, JsonObject msg) throws Exception {
		try {
			if (LOG.isDebugEnabled())
				LOG.debug("ENCODE [" + getFullName() + "] @"
						+ pctx.buf.position());
			JsonElement bre = msg.get("!raw");
			if (bre != null) {
				byte br[] = U.decode64(bre.getAsString());
				if (msg.has("@useraw")) {
					if (LOG.isDebugEnabled()) {
						LOG.debug("ENCODE USE RAW [" + U.dump(br) + "]");
					}
					pctx.buf.put(br);
					return;
				}
			}
			GroupCodexContext ctx = new GroupCodexContext(pctx);
			try {
				if (LOG.isTraceEnabled()) {
					for (int i=0, il=codex.length; i<il; i++) {
						int p = ctx.buf.position();
						codex[i].encode(ctx, msg);
						int p2 = ctx.buf.position();
						LOG.trace("ENCODE ["+codex[i].getFullName()+"]  "+p+"  ["+U.dump(ctx.buf.array(), p, p2-p)+"]");
					}
				} else {
					for (int i = 0, il = codex.length; i < il; i++) {
						codex[i].encode(ctx, msg);
					}
				}
				LOG.info("HERE >>> "+U.dump(ctx));
			} finally {
				ctx.close();
			}
//			if (pctx.parent == null) {
//				msg.addProperty("@raw", U.encode64(pctx.buf.array(), 0, pctx.buf.position()));
//			}
		} catch (CodexException e) {
			throw new CodexException("Codex error " + getFullName() + ": "
					+ e.getMessage(), e);
		}
	}

	public class SubCodexContext extends CodexContext {
		public SubCodexContext(CodexContext parent) {
			super(parent);
		}
	}

	public class GroupCodexContext extends CodexContext {
		private int xlength;

		GroupLength lengthCodex;
		int lengthPos;

		public GroupCodexContext(CodexContext parent) {
			super(parent);
		}
		
		public int getLength() {
			LOG.info("GET LENGTH "+xlength);
			return xlength;
		}

		public void setLength(int length) {
			LOG.info("SET LENGTH "+length);
			this.xlength = length;
		}

		@Override
		public void close() throws Exception {
			if (lengthCodex != null) {
				buf.position(lengthPos);
				lengthCodex.encodeLength(this, xlength);
			}
			parent.buf.put(buf.array(), 0, xlength);
			LOG.info("CLOSE "+U.dump(GroupCodexContext.this));
			super.close();
		}

	}
}
