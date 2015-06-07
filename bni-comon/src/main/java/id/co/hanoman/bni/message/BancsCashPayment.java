package id.co.hanoman.bni.message;

import id.co.hanoman.U;

import java.io.Serializable;
import java.math.BigDecimal;

@U.IgnoreNull
public class BancsCashPayment implements BancsData, Serializable {
	private static final long serialVersionUID = 7886551562322383480L;

	String prefix;
	String branchNo;
	String glCode;
	String glAccountNo;
	BigDecimal nominal;
	String filler1;
	String glAccountName;
	String filler2;
	String promo;
	String filler3;
	String currencyTrx;
	BigDecimal nominalTrx;
	String currencyBase;
	BigDecimal nominalBase;
	BigDecimal commission;
	BigDecimal change;
	String rateType;
	String filler4;
	String description;
	String uraian1;
	String uraian2;
	
	public String getHeaderOutputType() {
		return null;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getBranchNo() {
		return branchNo;
	}

	public void setBranchNo(String branchNo) {
		this.branchNo = branchNo;
	}

	public String getGlCode() {
		return glCode;
	}

	public void setGlCode(String glCode) {
		this.glCode = glCode;
	}

	public String getGlAccountNo() {
		return glAccountNo;
	}

	public void setGlAccountNo(String glAccountNo) {
		this.glAccountNo = glAccountNo;
	}

	public BigDecimal getNominal() {
		return nominal;
	}

	public void setNominal(BigDecimal nominal) {
		this.nominal = nominal;
	}

	public String getFiller1() {
		return filler1;
	}

	public void setFiller1(String filler1) {
		this.filler1 = filler1;
	}

	public String getGlAccountName() {
		return glAccountName;
	}

	public void setGlAccountName(String glAccountName) {
		this.glAccountName = glAccountName;
	}

	public String getFiller2() {
		return filler2;
	}

	public void setFiller2(String filler2) {
		this.filler2 = filler2;
	}

	public String getPromo() {
		return promo;
	}

	public void setPromo(String promo) {
		this.promo = promo;
	}

	public String getFiller3() {
		return filler3;
	}

	public void setFiller3(String filler3) {
		this.filler3 = filler3;
	}

	public String getCurrencyTrx() {
		return currencyTrx;
	}

	public void setCurrencyTrx(String currencyTrx) {
		this.currencyTrx = currencyTrx;
	}

	public BigDecimal getNominalTrx() {
		return nominalTrx;
	}

	public void setNominalTrx(BigDecimal nominalTrx) {
		this.nominalTrx = nominalTrx;
	}

	public String getCurrencyBase() {
		return currencyBase;
	}

	public void setCurrencyBase(String currencyBase) {
		this.currencyBase = currencyBase;
	}

	public BigDecimal getNominalBase() {
		return nominalBase;
	}

	public void setNominalBase(BigDecimal nominalBase) {
		this.nominalBase = nominalBase;
	}

	public BigDecimal getCommission() {
		return commission;
	}

	public void setCommission(BigDecimal commission) {
		this.commission = commission;
	}

	public BigDecimal getChange() {
		return change;
	}

	public void setChange(BigDecimal change) {
		this.change = change;
	}

	public String getRateType() {
		return rateType;
	}

	public void setRateType(String rateType) {
		this.rateType = rateType;
	}

	public String getFiller4() {
		return filler4;
	}

	public void setFiller4(String filler4) {
		this.filler4 = filler4;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUraian1() {
		return uraian1;
	}

	public void setUraian1(String uraian1) {
		this.uraian1 = uraian1;
	}

	public String getUraian2() {
		return uraian2;
	}

	public void setUraian2(String uraian2) {
		this.uraian2 = uraian2;
	}
}
