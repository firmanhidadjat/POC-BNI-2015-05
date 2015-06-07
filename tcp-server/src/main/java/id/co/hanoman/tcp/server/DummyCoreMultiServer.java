package id.co.hanoman.tcp.server;

import id.co.hanoman.bni.message.Bancs;
import id.co.hanoman.bni.message.BancsOk;
import id.co.hanoman.bni.message.BancsPrintingReceipt;

import java.math.BigDecimal;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.camel.Exchange;

public class DummyCoreMultiServer {

	public void fooService(Exchange exchange) throws Exception {
		List<Bancs> msgs = new LinkedList<Bancs>();
		
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
//		data.setBalance2CurrCode("");
//		data.setSwiftRef("");
		data.setBaseFca("181360209001350");
		data.setTxnCurrencyCode("IDR");
		data.setExchangeRate("000000");
		data.setBranchName("BEKASI");
		data.setTxnDescription("SETOR TUNAI");
		data.setBranchNo("0181");
		data.setNominal(new BigDecimal("6000000"));

		Bancs msg = new Bancs();
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
		
		msg.setData(data);
		msgs.add(msg);
		
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
		msgs.add(msg);
		
		exchange.getOut().setBody(msgs);
	}

}
