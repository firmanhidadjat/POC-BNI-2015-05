package id.co.hanoman.bni.sms;

import id.co.hanoman.U;
import id.co.hanoman.bni.codex.SignedDecimalCodex;
import id.co.hanoman.bni.message.Bancs;
import id.co.hanoman.bni.message.BancsCashPayment;
import id.co.hanoman.codex.Codex;
import id.co.hanoman.codex.CodexContext;
import id.co.hanoman.codex.CodexFactory;
import id.co.hanoman.codex.ValueHandler;
import id.co.hanoman.codex.netty.NettyCodex;
import id.co.hanoman.netty.ConnectionInfo;

import java.math.BigDecimal;

import org.apache.camel.Exchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CodexHelper {
	private static final Logger LOG = LoggerFactory.getLogger(CodexHelper.class);
	
	static Class<?>[] depends = {
		ConnectionInfo.class, NettyCodex.class, CodexFactory.class, ValueHandler.class, SignedDecimalCodex.class
	};
	
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
	
	public void composeCoreReq(Exchange exchange) throws Exception {
		exchange.setProperty("req", exchange.getIn().getBody());
		
		Bancs msg = new Bancs();
		msg.setHeaderFlag("0");
		msg.setHeaderDate("00000000");
		msg.setHeaderSequenceNumber("**");
		msg.setHeaderHostTerminal("      ");
		msg.setHeaderInstitutionNumber("003");
		msg.setHeaderBranch("0269");
		msg.setHeaderTerminal("188");
		msg.setHeaderTeller("42974");
		msg.setHeaderTransactionCode("020010");
		msg.setHeaderHostJournal("000000");
		msg.setHeaderSupervisorId("000000");

		BancsCashPayment data = new BancsCashPayment();
		data.setPrefix("0");
		data.setGlAccountNo("209001350");
		data.setGlAccountName("KAS NEGARA PERSEPSI");
		data.setCurrencyTrx("IDR");
		data.setCurrencyBase("IDR");
		data.setNominalBase(new BigDecimal("1245000000"));
		data.setDescription("SIPN-SSP-C-003738101423000");
		data.setBranchNo("0269");
		data.setGlCode("360");
		data.setNominal(new BigDecimal("1245000000"));
		data.setNominalTrx(new BigDecimal("1245000000"));
		data.setCommission(BigDecimal.ZERO);
		data.setChange(BigDecimal.ZERO);
		data.setRateType("00");
		
		msg.setData(data);
		exchange.getIn().setBody(msg);
	}
	
	public void processCoreReply(Exchange exchange) throws Exception {
		LOG.info("CORE RESP "+U.dump(exchange.getIn().getBody()));
		
		exchange.getIn().setBody(exchange.getProperty("req"));
	}
}
