package id.co.hanoman;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.security.Key;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Node;

public abstract class U {
	protected static final char[] hexArray = "0123456789ABCDEF".toCharArray();
	private static final int MAX_DUMP_DEPTH = 10;
	
	public static String bytesToHex(byte[] bytes) {
	    char[] hexChars = new char[bytes.length * 2];
	    for ( int j = 0; j < bytes.length; j++ ) {
	        int v = bytes[j] & 0xFF;
	        hexChars[j * 2] = hexArray[v >>> 4];
	        hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	    }
	    return new String(hexChars);
	}
	
	public static byte[] read(InputStream in) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte bb[] = new byte[1024];
		int len;
		while ((len = in.read(bb)) >= 0) {
			out.write(bb, 0, len);
		}
		return out.toByteArray();
	}
	
	static ThreadLocal<Integer> dumpTab = new ThreadLocal<Integer>();
	
	public static String dump(Object obj) {
		if (dumpTab.get() != null) throw new RuntimeException("RECURSIVE CALL");
		dumpTab.set(0);
		try {
			DumpOutputStream dos = new DumpOutputStream();
			PrintWriter out = new PrintWriter(dos);
			
			dump(out, null, obj, new IdentityHashMap<Object, String>(), 0);
			
			out.close();
			return new String(dos.toByteArray());
		} finally {
			dumpTab.remove();
		}
	}
	
	public static String dumpName(Class<?> cz) {
		if (cz.isArray()) {
			return dumpName(cz.getComponentType())+"[]";
		}
		return cz.getName();
	}
	
	public static void dump(PrintWriter out, Object ref, Object o, IdentityHashMap<Object, String> rec, int depth) {
		String recID = rec.get(o);
		if (o != null && recID == null) {
			rec.put(o, dumpName(o.getClass())+"@"+Integer.toHexString(System.identityHashCode(o)).toUpperCase());
		}
		if (o == null) {
			if (ref instanceof Method) {
				out.println(dumpName(((Method) ref).getReturnType())+" NULL");
			} else {
				out.println("NULL");
			}
		} else if (o instanceof Number) {
			out.println(dumpName(o.getClass())+" ["+o+"]");
		} else if (o instanceof String) {
			out.println(dumpName(o.getClass())+" ["+o+"]");
		} else if (recID != null) {
			out.println(dumpName(o.getClass())+" RECURSIVE "+recID);
		} else if (depth > MAX_DUMP_DEPTH) {
			out.println("TOO DEEP "+dumpName(o.getClass())+"@"+Integer.toHexString(System.identityHashCode(o)).toUpperCase());
		} else if (o instanceof byte[]) {
			byte bb[] = (byte[]) o;
			boolean ascii = true;
			for (int i=0, il=bb.length; i<il && ascii; i++) {
				byte b = bb[i];
				ascii = (b >= 10 && b < 128);
			}
			if (ascii) {
				try {
					out.println(dumpName(o.getClass())+" "+bb.length+" ["+URLEncoder.encode(new String(bb), "UTF8").replaceAll("\\+", " ")+"]");
				} catch (UnsupportedEncodingException e) {}
			} else {
				out.println(dumpName(o.getClass())+" "+bb.length+" HEX ["+bytesToHex(bb)+"]");
			}
		} else if (o instanceof Key) {
			out.println(dumpName(o.getClass())+" HEX ["+bytesToHex(((Key) o).getEncoded())+"]");
//		} else if (o instanceof JsonElement) {
//			out.flush();
//			dumpTab.set(dumpTab.get()+1);
//			out.println(dumpName(o.getClass())+" [");
//			
//			out.println(pgson.toJson((JsonElement) o));
//
//			out.flush();
//			dumpTab.set(dumpTab.get()-1);
//			out.println("]");
		} else if (o instanceof Source) {
			try {
				out.flush();
				out.println();
				TransformerFactory tf = TransformerFactory.newInstance();
			    Transformer transformer = tf.newTransformer();
			    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			    transformer.transform((Source) o, new StreamResult(out));
				out.flush();
			} catch (Exception e) {
				out.print(" {exception:"+e.getMessage()+"} ");
			}
		} else if (o instanceof Node) {
			try {
				out.flush();
				out.println();
				TransformerFactory tf = TransformerFactory.newInstance();
			    Transformer transformer = tf.newTransformer();
			    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			    transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			    transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			    transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			    transformer.transform(new DOMSource((Node) o), new StreamResult(out));
				out.flush();
			} catch (Exception e) {
				out.print(" {exception:"+e.getMessage()+"} ");
			}
		} else if (o.getClass().isArray()) {
			int len = Array.getLength(o);
			if (len == 0) {
				out.println(dumpName(o.getClass())+" [size:"+len+"]");
			} else {
				out.flush();
				dumpTab.set(dumpTab.get()+1);
				out.println(dumpName(o.getClass())+" [size:"+len);
				for (int i=0, idx=1; i<len; i++, idx++) {
					out.print(idx+" = ");
					dump(out, null, Array.get(o, i), rec, depth+1);
				}
				out.flush();
				dumpTab.set(dumpTab.get()-1);
				out.println("]");
			}
//		} else if (o.getClass().getName().startsWith("org.apache.")) {
//			out.println(dumpName(o.getClass())+" ["+o+"]");
		} else if (o instanceof Collection<?>) {
			Collection<?> col = (Collection<?>) o;
			out.flush();
			dumpTab.set(dumpTab.get()+1);
			out.println(dumpName(o.getClass())+" [size:"+col.size());
			int idx = 1;
			for (Object co : col) {
				out.print(idx+" = ");
				dump(out, null, co, rec, depth+1);
				idx ++;
			}
			out.flush();
			dumpTab.set(dumpTab.get()-1);
			out.println("]");
		} else if (o instanceof Map<?, ?>) {
			Map<?, ?> map = (Map<?, ?>) o;
			out.flush();
			dumpTab.set(dumpTab.get()+1);
			out.println(dumpName(o.getClass())+" [size:"+map.size());
			for (Map.Entry<?, ?> me : map.entrySet()) {
				out.print("["+me.getKey().toString()+"] = ");
				dump(out, null, me.getValue(), rec, depth+1);
			}
			out.flush();
			dumpTab.set(dumpTab.get()-1);
			out.println("]:MAP("+o.getClass().getName()+")");
		} else if (o.getClass().getName().startsWith("java")) {
			out.println(dumpName(o.getClass())+" ["+o+"]");
		} else {
			try {
				Method m = o.getClass().getMethod("dump", PrintWriter.class);
				m.invoke(o, out);
				return;
			} catch (NoSuchMethodException e) {
				// IGNORE
			} catch (Exception e) {
				out.print(" {exception:"+e.getMessage()+"} ");
			}
			out.flush();
			dumpTab.set(dumpTab.get()+1);
			out.println(dumpName(o.getClass())+"@"+Integer.toHexString(System.identityHashCode(o)).toUpperCase()+" {");
			for (Method m : o.getClass().getMethods()) {
				String mn = m.getName();
				if (m.getDeclaringClass().getName().startsWith("java")) {
					// SKIP
				} else if (m.getAnnotation(Ignore.class) != null) {
					// Ignore
				} else if (m.getAnnotation(SystemID.class) != null) {
					try {
						Object result = m.invoke(o);
						if (m.getName().startsWith("get")) {
							out.print(Character.toLowerCase(mn.charAt(3))+mn.substring(4));
						} else {
							out.print(Character.toLowerCase(mn.charAt(2))+mn.substring(3));
						}
						out.print(" = ");
						if (result != null) {
							out.println(dumpName(result.getClass())+"@"+Integer.toHexString(System.identityHashCode(result)).toUpperCase());
						} else {
							out.println("NULL");
						}
					} catch (Exception e) {
						// IGNORE
					}
				} else if (!m.getReturnType().equals(Void.class) && m.getParameterTypes().length == 0 && ((mn.startsWith("get") && mn.length()>3)|| (mn.startsWith("is") && mn.length()>2))) {
					try {
						Object result = m.invoke(o);
						if (m.getName().startsWith("get")) {
							out.print(Character.toLowerCase(mn.charAt(3))+mn.substring(4));
						} else {
							out.print(Character.toLowerCase(mn.charAt(2))+mn.substring(3));
						}
						out.print(" = ");
						dump(out, m, result, rec, depth+1);
					} catch (Exception e) {
						// IGNORE
					}
				}
			}
			out.flush();
			dumpTab.set(dumpTab.get()-1);
			out.println("}");
//			out.println(o.getClass().getName()+" ["+o+"]");
		}
	}
	
	public static class DumpOutputStream extends OutputStream {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		boolean nl = false;
		int p = 0;
		byte tabz[] = "                                                                                                            ".getBytes();
		
		@Override
		public void write(byte[] bb, int off, int len) throws IOException {
			int tab = dumpTab.get();
			for (int i=0, p=off; i<len; i++, p++) {
				byte b = bb[p];
				if (nl) {
					if (b == '\n' || b == '\r') {
						out.write(b);
					} else {
						out.write(tabz, 0, Math.min(tab*3, tabz.length));
						out.write(b);
						nl = false;
					}
				} else if (b == '\n') {
					out.write(b);
					nl = true;
				} else {
					out.write(b);
				}
			}
		}

		@Override
		public void write(int b) throws IOException {
			int tab = dumpTab.get();
			if (nl) {
				if (b == '\n' || b == '\r') {
					out.write(b);
				} else {
					out.write(tabz, 0, Math.min(tab*3, tabz.length));
					out.write(b);
					nl = false;
				}
			} else if (b == '\n') {
				out.write(b);
				nl = true;
			} else {
				out.write(b);
			}
		}
		
		@Override
		public void close() throws IOException {
			out.close();
			super.close();
		}

		public byte[] toByteArray() {
			return out.toByteArray();
		}
	}
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Ignore {
		
	}
	
	@Target(ElementType.METHOD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface SystemID {
		
	}
	
	public interface Streamer {
		void stream(InputStream in);
	}
	
	public static void open(File file, Streamer streamer) {
		InputStream in = null;
		try {
			in = new FileInputStream(file);
			streamer.stream(in);
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
		
	public static void open(String name, Streamer streamer) {
		InputStream in = null;
		try {
			in = new FileInputStream(name);
			streamer.stream(in);
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
