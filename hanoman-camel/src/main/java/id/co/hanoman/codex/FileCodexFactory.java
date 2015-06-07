package id.co.hanoman.codex;

import id.co.hanoman.config.Config;
import id.co.hanoman.config.ConfigXML;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileCodexFactory extends CodexFactory {
	private static final Logger LOG = LoggerFactory.getLogger(FileCodexFactory.class);
	
	File path;

	public FileCodexFactory(File path) {
		this.path = path;
		File f = new File(path, "codex-factory.properties");
		if (f.isFile()) {
			InputStream in = null;
			try {
				props.load(in = new FileInputStream(f));
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
	}
	
	public void setPath(File path) {
		this.path = path;
	}
	
	public File getPath() {
		return path;
	}
	
	@Override
	protected Codex doGetCodex(String id) {
		Codex c = codexes.get(id);
		if (c == null) {
			File f = new File(path, id+".xml");
			if (f.isFile()) {
				if (LOG.isDebugEnabled()) LOG.debug("Load codex "+f.getPath());
				try {
					Config cfg = ConfigXML.load(f);
					c = new GroupCodex(this);
					c.init(null, id, cfg);
					codexes.put(id, c);
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
		return c;
	}

}
