package id.co.hanoman.config;

import java.math.BigDecimal;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class Config {
	private static final Logger LOG = LoggerFactory.getLogger(Config.class);
	protected final String url;
	protected final String path;

	public Config(String url, String path) {
		this.url = url;
		this.path = path;
	}
	
	public abstract Object getContext();
	
	public String getUrl() {
		return url;
	}
	
	public String getPath() {
		return path;
	}
	
	public abstract Config getConfig(String path);

	public abstract List<Config> getList(String path);

	public String getStringValue(String path) {
		return getStringValue(path, null);
	}
	
	public abstract String getStringValue(String path, String nvl);
	
	public Boolean getBooleanValue(String path) {
		return getBooleanValue(path, null);
	}
	
	public Boolean getBooleanValue(String path, Boolean nvl) {
		String str = null;
		try {
			str = getStringValue(path, null);
			Boolean v = str != null ? new Boolean(str) : nvl;
			if (LOG.isTraceEnabled()) LOG.trace("Config get value ["+getPath()+"]  ["+path+"] = ["+v+"]");
			return v;
		} catch (RuntimeException ex) {
			throw new RuntimeException("Config error get value ["+getPath()+"]  ["+path+"] = ["+str+"] "+ex.getMessage(), ex);
		}
	}

	public Integer getIntegerValue(String path) {
		return getIntegerValue(path, null);
	}

	public Integer getIntegerValue(String path, Integer nvl) {
		String str = null;
		try {
			str = getStringValue(path, null);
			Integer v = str != null ? new Integer(str) : nvl;
			if (LOG.isTraceEnabled()) LOG.trace("Config get value ["+getPath()+"]  ["+path+"] = ["+v+"]");
			return v;
		} catch (RuntimeException ex) {
			throw new RuntimeException("Config error get value ["+getPath()+"]  ["+path+"] = ["+str+"] "+ex.getMessage(), ex);
		}
	}

	public Long getLongValue(String path) {
		return getLongValue(path, null);
	}

	public Long getLongValue(String path, Long nvl) {
		String str = null;
		try {
			str = getStringValue(path, null);
			Long v = str != null ? new Long(str) : nvl;
			if (LOG.isTraceEnabled()) LOG.trace("Config get value ["+getPath()+"]  ["+path+"] = ["+v+"]");
			return v;
		} catch (RuntimeException ex) {
			throw new RuntimeException("Config error get value ["+getPath()+"]  ["+path+"] = ["+str+"] "+ex.getMessage(), ex);
		}
	}

	public BigDecimal getDecimalValue(String path) {
		return getDecimalValue(path, null);
	}
	
	public BigDecimal getDecimalValue(String path, BigDecimal nvl) {
		String str = null;
		try {
			str = getStringValue(path, null);
			BigDecimal v = str != null ? new BigDecimal(str) : nvl;
			if (LOG.isTraceEnabled()) LOG.trace("Config get value ["+getPath()+"]  ["+path+"] = ["+v+"]");
			return v;
		} catch (RuntimeException ex) {
			throw new RuntimeException("Config error get value ["+getPath()+"]  ["+path+"] = ["+str+"] "+ex.getMessage(), ex);
		}
	}
}
