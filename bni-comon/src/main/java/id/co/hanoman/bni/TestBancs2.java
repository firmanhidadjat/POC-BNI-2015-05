package id.co.hanoman.bni;

import id.co.hanoman.U;
import id.co.hanoman.bni.message.Bancs;
import id.co.hanoman.bni.message.BancsOk;
import id.co.hanoman.bni.message.BancsPrintingReceipt;
import id.co.hanoman.codex.Codex;
import id.co.hanoman.codex.CodexContext;
import id.co.hanoman.codex.CodexFactory;
import id.co.hanoman.codex.FileCodexFactory;
import id.co.hanoman.codex.PojoValueHandler;

import java.io.File;
import java.math.BigDecimal;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestBancs2 {
	private final static Logger LOG = LoggerFactory.getLogger(TestBancs2.class);

	public static void main(String[] args) {
		try {
			Bancs msg = new Bancs();
			int test = 0;
			switch (test) {
			case 1:
				BancsOk dataOk = new BancsOk();
				dataOk.setString1("0000");
				dataOk.setString2(" O.K.     ");
				dataOk.setString3("                                                                ");
				
				msg = new Bancs();
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
				
				msg.setData(dataOk);
				break;
			default:
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

				BancsPrintingReceipt data = new BancsPrintingReceipt();
				data.setAccountName("KAS NEGARA PERSEPSI");
				data.setAccountNo("181360209001350");
				data.setUserNo("49612");
				data.setBancsJournalNo("116544");
				data.setTrxDate(new Date());
				data.setTrxTime(new Date());
				data.setMnumonic("BFHP");
				data.setBalance(new BigDecimal("576102806"));
				data.setBalance2(BigDecimal.ZERO);
				data.setBalance2CurrCode("");
				data.setSwiftRef("");
				data.setBaseFca("181360209001350");
				data.setTxnCurrencyCode("IDR");
				data.setExchangeRate("000000");
				data.setBranchName("BEKASI");
				data.setTxnDescription("SETOR TUNAI");
				data.setBranchNo("0181");
				data.setNominal(new BigDecimal("6000000"));
				
				msg.setData(data);
				break;
			}
			
			CodexFactory cf = new FileCodexFactory(new File("src/main/resources/META-INF/codex"));
			
			PojoValueHandler valueHandler = new PojoValueHandler(cf);
			
			CodexContext cc = new CodexContext(valueHandler);
			
			Codex c = cf.getCodex("bancs-resp");
			
			c.encode(cc, msg);
			
			LOG.info("RAW "+U.dump(cc.getBytes()));
			
			cc = new CodexContext(valueHandler, cc.getBytes());
			
			LOG.info("REVERSE "+U.dump(c.decode(cc, null)));
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
		}
	}
}
