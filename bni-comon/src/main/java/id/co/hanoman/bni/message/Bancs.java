package id.co.hanoman.bni.message;

import id.co.hanoman.U;

import java.io.Serializable;

@U.IgnoreNull
public class Bancs implements Serializable {
	private static final long serialVersionUID = -6047829715175545083L;

	String headerMagiccode;
	Integer headerPacketLength;
	String headerFiller1a;
	String headerDataLength;
	String headerFiller1b;
	String headerSequenceNumber;
	String headerFiller2;
	String headerHostTerminal;
	String headerInstitutionNumber;
	String headerBranch;
	String headerTerminal;
	String headerTeller;
	String headerTransactionCode;
	String headerHostJournal;
	String headerDate;
	String headerFlag;
	String headerFiller4;
	String headerSupervisorId;
	String headerFiller5;
	String headerOutputType;

	BancsData data;

	public String getHeaderMagiccode() {
		return headerMagiccode;
	}

	public void setHeaderMagiccode(String headerMagiccode) {
		this.headerMagiccode = headerMagiccode;
	}

	public Integer getHeaderPacketLength() {
		return headerPacketLength;
	}

	public void setHeaderPacketLength(Integer headerPacketLength) {
		this.headerPacketLength = headerPacketLength;
	}

	public String getHeaderFiller1a() {
		return headerFiller1a;
	}

	public void setHeaderFiller1a(String headerFiller1a) {
		this.headerFiller1a = headerFiller1a;
	}

	public String getHeaderDataLength() {
		return headerDataLength;
	}

	public void setHeaderDataLength(String headerDataLength) {
		this.headerDataLength = headerDataLength;
	}

	public String getHeaderFiller1b() {
		return headerFiller1b;
	}

	public void setHeaderFiller1b(String headerFiller1b) {
		this.headerFiller1b = headerFiller1b;
	}

	public String getHeaderSequenceNumber() {
		return headerSequenceNumber;
	}

	public void setHeaderSequenceNumber(String headerSequenceNumber) {
		this.headerSequenceNumber = headerSequenceNumber;
	}

	public String getHeaderFiller2() {
		return headerFiller2;
	}

	public void setHeaderFiller2(String headerFiller2) {
		this.headerFiller2 = headerFiller2;
	}

	public String getHeaderHostTerminal() {
		return headerHostTerminal;
	}

	public void setHeaderHostTerminal(String headerHostTerminal) {
		this.headerHostTerminal = headerHostTerminal;
	}

	public String getHeaderInstitutionNumber() {
		return headerInstitutionNumber;
	}

	public void setHeaderInstitutionNumber(String headerInstitutionNumber) {
		this.headerInstitutionNumber = headerInstitutionNumber;
	}

	public String getHeaderBranch() {
		return headerBranch;
	}

	public void setHeaderBranch(String headerBranch) {
		this.headerBranch = headerBranch;
	}

	public String getHeaderTerminal() {
		return headerTerminal;
	}

	public void setHeaderTerminal(String headerTerminal) {
		this.headerTerminal = headerTerminal;
	}

	public String getHeaderTeller() {
		return headerTeller;
	}

	public void setHeaderTeller(String headerTeller) {
		this.headerTeller = headerTeller;
	}

	public String getHeaderTransactionCode() {
		return headerTransactionCode;
	}

	public void setHeaderTransactionCode(String headerTransactionCode) {
		this.headerTransactionCode = headerTransactionCode;
	}

	public String getHeaderHostJournal() {
		return headerHostJournal;
	}

	public void setHeaderHostJournal(String headerHostJournal) {
		this.headerHostJournal = headerHostJournal;
	}

	public String getHeaderDate() {
		return headerDate;
	}

	public void setHeaderDate(String headerDate) {
		this.headerDate = headerDate;
	}

	public String getHeaderFlag() {
		return headerFlag;
	}

	public void setHeaderFlag(String headerFlag) {
		this.headerFlag = headerFlag;
	}

	public String getHeaderFiller4() {
		return headerFiller4;
	}

	public void setHeaderFiller4(String headerFiller4) {
		this.headerFiller4 = headerFiller4;
	}

	public String getHeaderSupervisorId() {
		return headerSupervisorId;
	}

	public void setHeaderSupervisorId(String headerSupervisorId) {
		this.headerSupervisorId = headerSupervisorId;
	}

	public String getHeaderFiller5() {
		return headerFiller5;
	}

	public void setHeaderFiller5(String headerFiller5) {
		this.headerFiller5 = headerFiller5;
	}
	
	public String getHeaderOutputType() {
		return headerOutputType;
	}
	
	public void setHeaderOutputType(String headerOutputType) {
		this.headerOutputType = headerOutputType;
	}

	public BancsData getData() {
		return data;
	}

	@SuppressWarnings("unchecked")
	public <T extends BancsData> T getData(Class<T> type) {
		return (T) data;
	}

	public void setData(BancsData data) {
		this.data = data;
		setHeaderOutputType(data.getHeaderOutputType());
	}
	
	@Override
	public String toString() {
		return U.dump(this);
	}
}
