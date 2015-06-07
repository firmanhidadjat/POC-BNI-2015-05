package id.co.hanoman.codex;

import id.co.hanoman.config.Config;

import java.util.Map;
import java.util.Properties;
import java.util.WeakHashMap;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class CodexFactory {
	private final static Logger LOG = LoggerFactory.getLogger(CodexFactory.class);
	final Map<String, Codex> codexes = new WeakHashMap<String, Codex>();
	final Properties props = new Properties();
	final ScriptEngineManager manager = new ScriptEngineManager();

	protected abstract Codex doGetCodex(String id);
	
	public Codex getCodexByType(Codex parent, String type, Config config) throws Exception {
		String cn;
		int ix;
		if ((ix = type.lastIndexOf('.')) == -1) {
			cn = "id.co.hanoman.codex." + type.substring(0, 1).toUpperCase() + type.substring(1) + "Codex";
		} else {
			cn = "id.co.hanoman." + type.substring(0, ix+1) + "codex." + type.substring(ix+1, ix+2).toUpperCase() + type.substring(ix+2) + "Codex";
		}
		if (LOG.isTraceEnabled()) LOG.trace("Load codex ["+cn+"]");
		Codex codex = (Codex) Thread.currentThread().getContextClassLoader().loadClass(cn).getConstructor(CodexFactory.class).newInstance(this);
		codex.init(parent, config.getStringValue("@id"), config);
		return codex;
	}
	
	public final Codex getCodex(String id) {
		synchronized (codexes) {
			Codex c = doGetCodex(id);
			if (c == null && props.containsKey(id)) {
				String pid = props.getProperty(id);
				c = doGetCodex(pid);
			}
			if (c == null) {
				throw new RuntimeException("No codex '"+id+"'");
			}
			return c;
		}
	}
	
	public final ScriptEngine getScriptEngine(String type) {
		return manager.getEngineByName(type);
	}
	
}
