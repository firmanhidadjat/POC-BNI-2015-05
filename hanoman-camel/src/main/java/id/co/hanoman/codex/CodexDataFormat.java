package id.co.hanoman.codex;

import java.io.InputStream;
import java.io.OutputStream;

import org.apache.camel.Exchange;
import org.apache.camel.spi.DataFormat;

public class CodexDataFormat implements DataFormat {
	CodexFactory codexFactory;
	ValueHandler valueHandler;
	String codex;

	public CodexFactory getCodexFactory() {
		return codexFactory;
	}
	
	public void setCodexFactory(CodexFactory codexFactory) {
		this.codexFactory = codexFactory;
	}
	
	public ValueHandler getValueHandler() {
		return valueHandler;
	}
	
	public void setValueHandler(ValueHandler valueHandler) {
		this.valueHandler = valueHandler;
	}
	
	public String getCodex() {
		return codex;
	}
	
	public void setCodex(String codex) {
		this.codex = codex;
	}

	@Override
	public void marshal(Exchange exchange, Object graph, OutputStream stream) throws Exception {
		CodexContext cc = new CodexContext(valueHandler);
		codexFactory.getCodex(codex).encode(cc, graph);
		stream.write(cc.readBytes());
	}

	@Override
	public Object unmarshal(Exchange exchange, InputStream stream) throws Exception {
		CodexContext cc = new CodexContext(valueHandler);
		return codexFactory.getCodex(codex).decode(cc, null);
	}

}
