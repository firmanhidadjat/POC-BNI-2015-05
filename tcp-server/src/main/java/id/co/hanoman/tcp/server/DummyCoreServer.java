package id.co.hanoman.tcp.server;

import id.co.hanoman.bni.message.Bancs;
import id.co.hanoman.bni.message.BancsPrintingReceipt;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.camel.Exchange;

public class DummyCoreServer {

	public void fooService(Exchange exchange) throws Exception {
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
		
		exchange.getOut().setBody(msg);
	}

}
