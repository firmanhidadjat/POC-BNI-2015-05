package id.co.hanoman.bni.message;

import id.co.hanoman.U;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@U.IgnoreNull
public class BancsPrintingReceipt implements BancsData, Serializable {
	private static final long serialVersionUID = 9077070111434407051L;

	String accountName;
	String accountNo;
	String branchNo;
	String userNo;
	String bancsJournalNo;
	Date trxDate;
	Date trxTime;
	String mnumonic;
	BigDecimal nominal;
	BigDecimal balance;
	BigDecimal balance2;
	String balance2CurrCode;
	String swiftRef;
	String baseFca;
	String txnCurrencyCode;
	String exchangeRate;
	String branchName;
	String txnDescription;
	
	public String getHeaderOutputType() {
		return "09";
	}

	public String getAccountName() {
		return accountName;
	}

	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	public String getAccountNo() {
		return accountNo;
	}

	public void setAccountNo(String accountNo) {
		this.accountNo = accountNo;
	}

	public String getBranchNo() {
		return branchNo;
	}

	public void setBranchNo(String branchNo) {
		this.branchNo = branchNo;
	}

	public String getUserNo() {
		return userNo;
	}

	public void setUserNo(String userNo) {
		this.userNo = userNo;
	}

	public String getBancsJournalNo() {
		return bancsJournalNo;
	}

	public void setBancsJournalNo(String bancsJournalNo) {
		this.bancsJournalNo = bancsJournalNo;
	}
	
	@U.Format("dd/MM/yyyy")
	public Date getTrxDate() {
		return trxDate;
	}
	
	public void setTrxDate(Date trxDate) {
		this.trxDate = trxDate;
	}
	
	@U.Format("HH:mm:ss")
	public Date getTrxTime() {
		return trxTime;
	}
	
	public void setTrxTime(Date trxTime) {
		this.trxTime = trxTime;
	}

	public String getMnumonic() {
		return mnumonic;
	}

	public void setMnumonic(String mnumonic) {
		this.mnumonic = mnumonic;
	}

	public BigDecimal getNominal() {
		return nominal;
	}

	public void setNominal(BigDecimal nominal) {
		this.nominal = nominal;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public BigDecimal getBalance2() {
		return balance2;
	}

	public void setBalance2(BigDecimal balance2) {
		this.balance2 = balance2;
	}

	public String getBalance2CurrCode() {
		return balance2CurrCode;
	}

	public void setBalance2CurrCode(String balance2CurrCode) {
		this.balance2CurrCode = balance2CurrCode;
	}

	public String getSwiftRef() {
		return swiftRef;
	}

	public void setSwiftRef(String swiftRef) {
		this.swiftRef = swiftRef;
	}

	public String getBaseFca() {
		return baseFca;
	}

	public void setBaseFca(String baseFca) {
		this.baseFca = baseFca;
	}

	public String getTxnCurrencyCode() {
		return txnCurrencyCode;
	}

	public void setTxnCurrencyCode(String txnCurrencyCode) {
		this.txnCurrencyCode = txnCurrencyCode;
	}

	public String getExchangeRate() {
		return exchangeRate;
	}

	public void setExchangeRate(String exchangeRate) {
		this.exchangeRate = exchangeRate;
	}

	public String getBranchName() {
		return branchName;
	}

	public void setBranchName(String branchName) {
		this.branchName = branchName;
	}

	public String getTxnDescription() {
		return txnDescription;
	}

	public void setTxnDescription(String txnDescription) {
		this.txnDescription = txnDescription;
	}
}
