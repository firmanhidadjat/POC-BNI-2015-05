package id.co.hanoman.codex;

import id.co.hanoman.U;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PojoValueHandler implements ValueHandler {
	private static final Logger LOG = LoggerFactory.getLogger(JsonValueHandler.class);
	private final static Map<String, Map<String, Method>> getter = new WeakHashMap<>();
	private final static Map<String, Map<String, Method>> setter = new WeakHashMap<>();
	
	private final Map<Codex, Class<?>> pojos = new HashMap<>();
	
	public PojoValueHandler(CodexFactory codexFactory) {
		for (Entry<Object, Object> pe : codexFactory.props.entrySet()) {
			String pek = (String) pe.getKey();
			String pev = (String) pe.getValue();
			if (pek.startsWith("pojo:")) {
				pek = pek.substring(5);
				try {
					pojos.put(codexFactory.getCodex(pek), Thread.currentThread().getContextClassLoader().loadClass(pev));
				} catch (ClassNotFoundException e) {
					LOG.warn("Class not found for '"+pek+"' "+e.getMessage(), e);
				}
			}
		}
	}
	
	public String fixName(String name) {
		StringBuilder sb = new StringBuilder();
		for (int i=0, il=name.length(), state = 0; i<il; i++) {
			char ch = name.charAt(i);
			if (state == 0) {
				if (Character.isAlphabetic(ch)) {
					sb.append(Character.toUpperCase(ch));
					state = 1;
				} else {
					sb.append(ch);
				}
			} else if (state == 1) {
				if (Character.isJavaIdentifierPart(ch)) {
					sb.append(ch);
				} else if (Character.isWhitespace(ch)) {
					state = 2;
				} else {
					sb.append("_");
				}
			} else if (state == 2) {
				if (Character.isAlphabetic(ch)) {
					sb.append(Character.toUpperCase(ch));
					state = 1;
				} else if (Character.isWhitespace(ch)) {
					// skip
				} else {
					sb.append(ch);
					state = 1;
				}
			}
		}
		return sb.toString();
	}
	
	public Method getSetter(Class<?> oc, String field, Object value) {
		synchronized (setter) {
			if (field.startsWith("@")) return null;
			String k = oc.getName();
			Map<String, Method> map = setter.get(k);
			if (map == null) setter.put(k, map = new HashMap<String, Method>());
			String fx;
			if (value != null) {
				fx = field + ":" + value.getClass().getName();
			} else {
				fx = field;
			}
			Method m = map.get(fx);
			if (m == null) {
				String fn = fixName(field);
				if (value != null) {
					String sfn = "set"+fn;
					try {
						m = oc.getMethod(sfn, value.getClass());
					} catch (NoSuchMethodException | SecurityException e) {
						Method ms[] = oc.getMethods();
						for (int i=0, il=ms.length; i<il; i++) {
							m = ms[i];
							if (m.getParameterTypes().length == 1 && m.getName().equals(sfn)) {
								map.put(fx,  m);
								return m;
							}
						}
						throw new RuntimeException("Field ["+field+"] "+e.getMessage(), e);
					}
				} else {
					try {
						m = oc.getMethod("set"+fn, Object.class);
					} catch (NoSuchMethodException | SecurityException e) {
						throw new RuntimeException("Field ["+field+"] "+e.getMessage(), e);
					}
				}
				map.put(fx, m);
			}
			return m;
		}
	}

	public Method getGetter(Class<?> oc, String field) {
		synchronized (getter) {
			if (field.startsWith("@")) return null;
			String k = oc.getName();
			Map<String, Method> map = getter.get(k);
			if (map == null) getter.put(k, map = new HashMap<String, Method>());
			Method m = map.get(field);
			if (m == null) {
				String fn = fixName(field);
				try {
					m = oc.getMethod("get"+fn);
				} catch (NoSuchMethodException | SecurityException e) {
					try {
						m = oc.getMethod("is"+fn);
					} catch (NoSuchMethodException | SecurityException e1) {
						throw new RuntimeException("Field ["+field+"] "+e1.getMessage(), e1);
					}
				}
				map.put(field, m);
			}
			return m;
		}
	}
	
	@Override
	public void setCodexValue(CodexContext ctx, Object obj, String field, Object value) {
		try {
			Method m = getSetter(obj.getClass(), field, value);
			if (m == null) return; // TODO SPECIALS
			if (value == null) {
				m.invoke(obj, value);
			} else {
				Class<?> vc = value.getClass();
				Class<?> mt = m.getParameterTypes()[0];
				if (vc.equals(mt)) {
					m.invoke(obj, value);
				} else if (mt.isAssignableFrom(vc)) {
					m.invoke(obj, value);
				} else if (mt.isAssignableFrom(String.class)) {
					if (String.class.isAssignableFrom(vc)) {
						m.invoke(obj, value);
					} else {
						m.invoke(obj, value.toString());
					}
				} else if (mt.isAssignableFrom(Integer.class)) {
					if (Integer.class.isAssignableFrom(vc)) {
						m.invoke(obj, value);
					} else if (String.class.isAssignableFrom(vc)) {
						m.invoke(obj, Integer.valueOf((String) value));
					} else {
						m.invoke(obj, Integer.valueOf(value.toString()));
					}
				} else {
					throw new RuntimeException("Field ["+field+"] mismatch "+mt.getName()+" from "+vc.getName());
				}
			}
		} catch (Exception e) {
			throw new RuntimeException("Field ["+field+"] "+e.getMessage(), e);
		}
	}

	@Override
	public void setCodexError(CodexContext ctx, Object obj, String field, String message) {
		// TODO
	}

	@Override
	public boolean hasCodexValue(CodexContext ctx, Object obj, String field) {
		try {
			return getGetter(obj.getClass(), field).invoke(obj) != null;
		} catch (IllegalArgumentException | InvocationTargetException | IllegalAccessException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}


	@SuppressWarnings("unchecked")
	public <T> T getCodexValue(CodexContext ctx, Object obj, String field, Class<T> type) {
		Object val;
		try {
			Method m = getGetter(obj.getClass(), field);
			if (m == null) return null;
			val = m.invoke(obj);
		} catch (Exception e) {
			throw new RuntimeException("Field ["+field+"] ["+obj+"] "+e.getMessage(), e);
		}
		if (val == null) {
			return null;
		} else if (type.isAssignableFrom(val.getClass())) {
			return (T) val;
		} else if (type.isAssignableFrom(String.class)) {
			if (val instanceof String) {
				return (T) val;
			}
			return (T) val.toString();
		} else if (type.isAssignableFrom(BigDecimal.class)) {
			if (val instanceof BigDecimal) {
				return (T) val;
			} else if (val instanceof String) {
				return (T) new BigDecimal((String) val);
			}
			return (T) new BigDecimal(val.toString());
		} else if (type.isAssignableFrom(Long.class)) {
			if (val instanceof Long) {
				return (T) val;
			} else if (val instanceof String) {
				return (T) Long.valueOf((String) val);
			}
			return (T) Long.valueOf(val.toString());
		} else if (type.isAssignableFrom(Double.class)) {
			if (val instanceof Double) {
				return (T) val;
			} else if (val instanceof String) {
				return (T) Double.valueOf((String) val);
			}
			return (T) Double.valueOf(val.toString());
		} else if (type.isAssignableFrom(Integer.class)) {
			if (val instanceof Integer) {
				return (T) val;
			} else if (val instanceof String) {
				return (T) Integer.valueOf((String) val);
			}
			return (T) Integer.valueOf(val.toString());
		}
		throw new RuntimeException("Field ["+field+"] Not supported "+type.getName()+" "+val);
	}
	
	@Override
	public Object newObject(Codex codex) {
		try {
			if (!pojos.containsKey(codex)) throw new RuntimeException("Unable to create new pojo for codex "+U.dump(codex));
			return pojos.get(codex).newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e.getMessage()+"\nCodex: "+U.dump(codex), e);
		}
	}
}
