package id.co.hanoman.bni;

import id.co.hanoman.U;
import id.co.hanoman.bni.message.Bancs;
import id.co.hanoman.bni.message.BancsCashPayment;
import id.co.hanoman.codex.Codex;
import id.co.hanoman.codex.CodexContext;
import id.co.hanoman.codex.CodexFactory;
import id.co.hanoman.codex.FileCodexFactory;
import id.co.hanoman.codex.PojoValueHandler;

import java.io.File;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestTCP2 {
	static Logger log = LoggerFactory.getLogger(TestTCP2.class);

	public static void main(String[] args) {
		try {
			CodexFactory cf = new FileCodexFactory(new File("src/main/resources/META-INF/codex"));
			
			PojoValueHandler valueHandler = new PojoValueHandler(cf);
			
			Codex c = cf.getCodex("bancs-req");
			
			Bancs msg = new Bancs();
			msg.setHeaderMagiccode(" ");
			msg.setHeaderSequenceNumber("0000");
			msg.setHeaderHostTerminal("000573");
			msg.setHeaderInstitutionNumber("003");
			msg.setHeaderBranch("0181");
			msg.setHeaderTerminal("573");
			msg.setHeaderTeller("49612");
			msg.setHeaderTransactionCode("020010");
			msg.setHeaderHostJournal("116544");
			msg.setHeaderDate("00004211");
			msg.setHeaderFlag("0000");
			msg.setHeaderSupervisorId("000000");

			BancsCashPayment data = new BancsCashPayment();
			data.setBranchNo("0269");
			data.setGlCode("360");
			data.setGlAccountNo("209001350");
			data.setNominal(new BigDecimal("124500"));
			data.setCurrencyTrx("IDR");
			data.setNominalTrx(new BigDecimal("124500"));
			data.setCurrencyBase("IDR");
			data.setNominalBase(new BigDecimal("124500"));
			data.setRateType("00");
			data.setDescription("SIPN-SSP-C-003738101423000");
			data.setPrefix("0");
			
			msg.setData(data);
			
			CodexContext cc = new CodexContext(valueHandler);
			c.encode(cc, msg);
			byte b1[] = cc.readBytes();
			
			log.info("DATA ["+U.dump(b1)+"]");
			
			Socket sock = new Socket("localhost", 5050);
			sock.getOutputStream().write(b1);
			InputStream in = sock.getInputStream();
			
			Thread.sleep(1000);
			byte b[] = new byte[8192];
			int len = in.read(b);
			
			c = cf.getCodex("bancs-resp");
			
			log.info("READ "+len+"  "+U.dump(b, 0, len));
			log.info("REPLY "+U.dump(c.decode(new CodexContext(valueHandler, b), null)));
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
}
