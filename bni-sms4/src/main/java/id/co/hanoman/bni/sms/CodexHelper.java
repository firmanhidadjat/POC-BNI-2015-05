package id.co.hanoman.bni.sms;

import id.co.hanoman.U;
import id.co.hanoman.codex.Codex;
import id.co.hanoman.codex.CodexContext;
import id.co.hanoman.codex.CodexFactory;
import id.co.hanoman.codex.ValueHandler;

import org.apache.camel.Exchange;

public class CodexHelper {
	CodexFactory codexFactory;
	ValueHandler valueHandler;
	String encode;
	String decode;
	
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
	
	public String getEncode() {
		return encode;
	}
	
	public void setEncode(String encode) {
		this.encode = encode;
	}
	
	public String getDecode() {
		return decode;
	}
	
	public void setDecode(String decode) {
		this.decode = decode;
	}

	public void decode(Exchange exchange) throws Exception {
		Codex c = codexFactory.getCodex(decode);
		CodexContext cc = new CodexContext(valueHandler, U.getBytes(exchange.getIn().getBody(String.class)));
		if (exchange.getPattern().isOutCapable()) {
			exchange.getOut().setBody(c.decode(cc, null));
		} else {
			exchange.getIn().setBody(c.decode(cc, null));
		}
	}
}
