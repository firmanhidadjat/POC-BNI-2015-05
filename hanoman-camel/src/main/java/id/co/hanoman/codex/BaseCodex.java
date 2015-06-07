package id.co.hanoman.codex;

import id.co.hanoman.config.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class BaseCodex implements Codex {
	private static final Logger LOG = LoggerFactory.getLogger(BaseCodex.class);
	protected String charset;
	protected final String DEFAULT_CHARSET = "ISO-8859-1";

	final CodexFactory factory;
	Codex parent;
	Config config;
	String id;
	String fid;
	
	public BaseCodex(CodexFactory factory) {
		this.factory = factory;
	}
	
	@Override
	public void init(Codex parent, String id, Config config) throws Exception {
		this.parent = parent;
		this.config = config;
		this.id = id;
		charset = config.getStringValue("@charset", parent instanceof BaseCodex ? ((BaseCodex) parent).charset : DEFAULT_CHARSET);
		if (charset == null) charset = DEFAULT_CHARSET;
		fid = config.getPath();
		if (fid.length() > 0) {
			fid = fid + "/" + id;
		} else {
			fid = id;
		}
		if (LOG.isTraceEnabled()) {
			LOG.trace("INIT ["+getFullName()+"] charset = ["+charset+"]");
		}
	}
	
	public CodexFactory getFactory() {
		return factory;
	}
	
	@Override
	public String getName() {
		return id;
	}
	
	@Override
	public String getFullName() {
		if (parent != null) return parent.getFullName() + "/" + getName();
		return getName();
	}
	
}
