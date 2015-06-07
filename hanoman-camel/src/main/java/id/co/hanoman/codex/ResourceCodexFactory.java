package id.co.hanoman.codex;

import id.co.hanoman.config.Config;
import id.co.hanoman.config.ConfigXML;

import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceCodexFactory extends CodexFactory {
	private static final Logger LOG = LoggerFactory.getLogger(ResourceCodexFactory.class);
	
	public ResourceCodexFactory() {
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/codex/codex-factory.properties");
		if (in != null) {
			try {
				props.load(in);
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			} finally {
				try {
					in.close();
				} catch (Exception e) {}
			}
		}
	}
	
	@Override
	protected Codex doGetCodex(String id) {
		synchronized (codexes) {
			Codex c = codexes.get(id);
			if (c == null) {
				InputStream in = null;
				try {
					in = Thread.currentThread().getContextClassLoader().getResourceAsStream("META-INF/codex/"+id+".xml");
					if (LOG.isTraceEnabled()) LOG.trace("LOAD CODEX [META-INF/codex/"+id+".xml] "+in);
					if (in == null) return null;
					Config cfg = ConfigXML.load(id, in);
					c = new GroupCodex(this);
					c.init(null, id, cfg);
					codexes.put(id, c);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (Exception e) {}
					}
				}
			}
			return c;
		}
	}

}
