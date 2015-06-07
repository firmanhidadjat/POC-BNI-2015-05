package id.co.hanoman.bni.message;

import id.co.hanoman.U;

import java.io.Serializable;

@U.IgnoreNull
public class Iso8583 implements Serializable {
	private static final long serialVersionUID = -5385679498938166035L;
	
	Integer packetLength;
	String messageType;
	
	String primaryAccountNumber;
	String processingCode;
	String amountTransaction;
	String amountSettlement;
	String amountCardholderBilling;
	String transmissionDateTime;
	String amountCardholderBillingFee;
	String conversionRateSettlement;
	String conversionRateCardholderBilling;
	String systemsTraceAuditNumber;
	String timeLocalTransaction;
	String dateLocalTransaction;
	String dateExpiration;
	String dateSettlement;
	String dateConversion;
	String dateCapture;
	String merchantType;
	String acquiringInstitutionCountryCode;
	String panExtendedCountryCode;
	String forwardingInstitutionCountryCode;
	String pointOfServiceEntryMode;
	String applicationPanNumber;
	String networkInternationalIdentifier;
	String pointOfServiceConditionCode;
	String pointOfServiceCaptureCode;
	String authorisingIdentificationResponseLength;
	String amountTransactionFee;
	String amountSettlementFee;
	String amountTransactionProcessingFee;
	String amountSettlementProcessingFee;
	String acquiringInstitutionIdentificationCode;
	String forwardingInstitutionIdentificationCode;
	String primaryAccountNumberExtended;
	String track2Data;
	String track3Data;
	String retrievalReferenceNumber;
	String authorisationIdentificationResponse;
	String responseCode;
	String serviceRestrictionCode;
	String cardAcceptorTerminalIdentification;
	String cardAcceptorIdentificationCode;
	String cardAcceptorName;
	String additionalResponseData;
	String track1Data;
	String additionalDataIso;
	String additionalDataNational;
	String additionalDataPrivate;
	String currencyCodeTransaction;
	String currencyCodeSettlement;
	String currencyCodeCardholderBilling;
	String personalIdentificationNumberData;
	String securityRelatedControlInformation;
	String additionalAmounts;
	String reservedIso1;
	String reservedIso2;
	String reservedNational1;
	String reservedNational2;
	String reservedNational3;
	String reasonCode;
	String reservedPrivate1;
	String reservedPrivate2;
	String reservedPrivate3;
	String messageAuthenticationCode;
	String bitMapTertiary;
	String settlementCode;
	String extendedPaymentCode;
	String receivingInstitutionCountryCode;
	String settlementInstitutionCountyCode;
	String networkManagementInformationCode;
	String messageNumber;
	String messageNumberLast;
	String dateAction;
	String creditsNumber;
	String creditsReversalNumber;
	String debitsNumber;
	String debitsReversalNumber;
	String transferNumber;
	String transferReversalNumber;
	String inquiriesNumber;
	String authorisationsNumber;
	String creditsProcesssingFeeAmount;
	String creditsTransactionFeeAmount;
	String debitsProcessingFeeAmount;
	String debitsTransactionFeeAmount;
	String creditsAmount;
	String creditsReversalAmount;
	String debitsAmount;
	String debitsReversalAmount;
	String originalDataElements;
	String fileUpdateCode;
	String fileSecurityCode;
	String responseIndicator;
	String serviceIndicator;
	String replacementAmounts;
	String messageSecurityCode;
	String amountNetSettlement;
	String payee;
	String settlementInstitutionIdentificationCode;
	String receivingInstitutionIdentificationCode;
	String slipNumber;
	String fromAccount;
	String toAccount;
	String transactionDescription;
	String reservedForIsoUse1;
	String reservedForIsoUse2;
	String reservedForIsoUse3;
	String reservedForIsoUse4;
	String reservedForIsoUse5;
	String reservedForIsoUse6;
	String reservedForIsoUse7;
	String reservedForNationalUse;
	String authorisingAgentInstitutionIdCode;
	String reservedForNationalUse1;
	String reservedForNationalUse2;
	String reservedForNationalUse3;
	String reservedForNationalUse4;
	String reservedForNationalUse5;
	String reservedForNationalUse6;
	String reservedForPrivateUse1;
	String reservedForPrivateUse2;
	String reservedForPrivateUse3;
	String reservedForPrivateUse4;
	String infoText;
	String networkManagementInformation;
	String issuerTraceId;
	String reservedForPrivateUse;
	String messageAuthenticationCode2;
	
	public Integer getPacketLength() {
		return packetLength;
	}
	public void setPacketLength(Integer packetLength) {
		this.packetLength = packetLength;
	}
	public String getMessageType() {
		return messageType;
	}
	public void setMessageType(String messageType) {
		this.messageType = messageType;
	}
	public String getPrimaryAccountNumber() {
		return primaryAccountNumber;
	}
	public void setPrimaryAccountNumber(String primaryAccountNumber) {
		this.primaryAccountNumber = primaryAccountNumber;
	}
	public String getProcessingCode() {
		return processingCode;
	}
	public void setProcessingCode(String processingCode) {
		this.processingCode = processingCode;
	}
	public String getAmountTransaction() {
		return amountTransaction;
	}
	public void setAmountTransaction(String amountTransaction) {
		this.amountTransaction = amountTransaction;
	}
	public String getAmountSettlement() {
		return amountSettlement;
	}
	public void setAmountSettlement(String amountSettlement) {
		this.amountSettlement = amountSettlement;
	}
	public String getAmountCardholderBilling() {
		return amountCardholderBilling;
	}
	public void setAmountCardholderBilling(String amountCardholderBilling) {
		this.amountCardholderBilling = amountCardholderBilling;
	}
	public String getTransmissionDateTime() {
		return transmissionDateTime;
	}
	public void setTransmissionDateTime(String transmissionDateTime) {
		this.transmissionDateTime = transmissionDateTime;
	}
	public String getAmountCardholderBillingFee() {
		return amountCardholderBillingFee;
	}
	public void setAmountCardholderBillingFee(String amountCardholderBillingFee) {
		this.amountCardholderBillingFee = amountCardholderBillingFee;
	}
	public String getConversionRateSettlement() {
		return conversionRateSettlement;
	}
	public void setConversionRateSettlement(String conversionRateSettlement) {
		this.conversionRateSettlement = conversionRateSettlement;
	}
	public String getConversionRateCardholderBilling() {
		return conversionRateCardholderBilling;
	}
	public void setConversionRateCardholderBilling(
			String conversionRateCardholderBilling) {
		this.conversionRateCardholderBilling = conversionRateCardholderBilling;
	}
	public String getSystemsTraceAuditNumber() {
		return systemsTraceAuditNumber;
	}
	public void setSystemsTraceAuditNumber(String systemsTraceAuditNumber) {
		this.systemsTraceAuditNumber = systemsTraceAuditNumber;
	}
	public String getTimeLocalTransaction() {
		return timeLocalTransaction;
	}
	public void setTimeLocalTransaction(String timeLocalTransaction) {
		this.timeLocalTransaction = timeLocalTransaction;
	}
	public String getDateLocalTransaction() {
		return dateLocalTransaction;
	}
	public void setDateLocalTransaction(String dateLocalTransaction) {
		this.dateLocalTransaction = dateLocalTransaction;
	}
	public String getDateExpiration() {
		return dateExpiration;
	}
	public void setDateExpiration(String dateExpiration) {
		this.dateExpiration = dateExpiration;
	}
	public String getDateSettlement() {
		return dateSettlement;
	}
	public void setDateSettlement(String dateSettlement) {
		this.dateSettlement = dateSettlement;
	}
	public String getDateConversion() {
		return dateConversion;
	}
	public void setDateConversion(String dateConversion) {
		this.dateConversion = dateConversion;
	}
	public String getDateCapture() {
		return dateCapture;
	}
	public void setDateCapture(String dateCapture) {
		this.dateCapture = dateCapture;
	}
	public String getMerchantType() {
		return merchantType;
	}
	public void setMerchantType(String merchantType) {
		this.merchantType = merchantType;
	}
	public String getAcquiringInstitutionCountryCode() {
		return acquiringInstitutionCountryCode;
	}
	public void setAcquiringInstitutionCountryCode(
			String acquiringInstitutionCountryCode) {
		this.acquiringInstitutionCountryCode = acquiringInstitutionCountryCode;
	}
	public String getPanExtendedCountryCode() {
		return panExtendedCountryCode;
	}
	public void setPanExtendedCountryCode(String panExtendedCountryCode) {
		this.panExtendedCountryCode = panExtendedCountryCode;
	}
	public String getForwardingInstitutionCountryCode() {
		return forwardingInstitutionCountryCode;
	}
	public void setForwardingInstitutionCountryCode(
			String forwardingInstitutionCountryCode) {
		this.forwardingInstitutionCountryCode = forwardingInstitutionCountryCode;
	}
	public String getPointOfServiceEntryMode() {
		return pointOfServiceEntryMode;
	}
	public void setPointOfServiceEntryMode(String pointOfServiceEntryMode) {
		this.pointOfServiceEntryMode = pointOfServiceEntryMode;
	}
	public String getApplicationPanNumber() {
		return applicationPanNumber;
	}
	public void setApplicationPanNumber(String applicationPanNumber) {
		this.applicationPanNumber = applicationPanNumber;
	}
	public String getNetworkInternationalIdentifier() {
		return networkInternationalIdentifier;
	}
	public void setNetworkInternationalIdentifier(
			String networkInternationalIdentifier) {
		this.networkInternationalIdentifier = networkInternationalIdentifier;
	}
	public String getPointOfServiceConditionCode() {
		return pointOfServiceConditionCode;
	}
	public void setPointOfServiceConditionCode(String pointOfServiceConditionCode) {
		this.pointOfServiceConditionCode = pointOfServiceConditionCode;
	}
	public String getPointOfServiceCaptureCode() {
		return pointOfServiceCaptureCode;
	}
	public void setPointOfServiceCaptureCode(String pointOfServiceCaptureCode) {
		this.pointOfServiceCaptureCode = pointOfServiceCaptureCode;
	}
	public String getAuthorisingIdentificationResponseLength() {
		return authorisingIdentificationResponseLength;
	}
	public void setAuthorisingIdentificationResponseLength(
			String authorisingIdentificationResponseLength) {
		this.authorisingIdentificationResponseLength = authorisingIdentificationResponseLength;
	}
	public String getAmountTransactionFee() {
		return amountTransactionFee;
	}
	public void setAmountTransactionFee(String amountTransactionFee) {
		this.amountTransactionFee = amountTransactionFee;
	}
	public String getAmountSettlementFee() {
		return amountSettlementFee;
	}
	public void setAmountSettlementFee(String amountSettlementFee) {
		this.amountSettlementFee = amountSettlementFee;
	}
	public String getAmountTransactionProcessingFee() {
		return amountTransactionProcessingFee;
	}
	public void setAmountTransactionProcessingFee(
			String amountTransactionProcessingFee) {
		this.amountTransactionProcessingFee = amountTransactionProcessingFee;
	}
	public String getAmountSettlementProcessingFee() {
		return amountSettlementProcessingFee;
	}
	public void setAmountSettlementProcessingFee(
			String amountSettlementProcessingFee) {
		this.amountSettlementProcessingFee = amountSettlementProcessingFee;
	}
	public String getAcquiringInstitutionIdentificationCode() {
		return acquiringInstitutionIdentificationCode;
	}
	public void setAcquiringInstitutionIdentificationCode(
			String acquiringInstitutionIdentificationCode) {
		this.acquiringInstitutionIdentificationCode = acquiringInstitutionIdentificationCode;
	}
	public String getForwardingInstitutionIdentificationCode() {
		return forwardingInstitutionIdentificationCode;
	}
	public void setForwardingInstitutionIdentificationCode(
			String forwardingInstitutionIdentificationCode) {
		this.forwardingInstitutionIdentificationCode = forwardingInstitutionIdentificationCode;
	}
	public String getPrimaryAccountNumberExtended() {
		return primaryAccountNumberExtended;
	}
	public void setPrimaryAccountNumberExtended(String primaryAccountNumberExtended) {
		this.primaryAccountNumberExtended = primaryAccountNumberExtended;
	}
	public String getTrack2Data() {
		return track2Data;
	}
	public void setTrack2Data(String track2Data) {
		this.track2Data = track2Data;
	}
	public String getTrack3Data() {
		return track3Data;
	}
	public void setTrack3Data(String track3Data) {
		this.track3Data = track3Data;
	}
	public String getRetrievalReferenceNumber() {
		return retrievalReferenceNumber;
	}
	public void setRetrievalReferenceNumber(String retrievalReferenceNumber) {
		this.retrievalReferenceNumber = retrievalReferenceNumber;
	}
	public String getAuthorisationIdentificationResponse() {
		return authorisationIdentificationResponse;
	}
	public void setAuthorisationIdentificationResponse(
			String authorisationIdentificationResponse) {
		this.authorisationIdentificationResponse = authorisationIdentificationResponse;
	}
	public String getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}
	public String getServiceRestrictionCode() {
		return serviceRestrictionCode;
	}
	public void setServiceRestrictionCode(String serviceRestrictionCode) {
		this.serviceRestrictionCode = serviceRestrictionCode;
	}
	public String getCardAcceptorTerminalIdentification() {
		return cardAcceptorTerminalIdentification;
	}
	public void setCardAcceptorTerminalIdentification(
			String cardAcceptorTerminalIdentification) {
		this.cardAcceptorTerminalIdentification = cardAcceptorTerminalIdentification;
	}
	public String getCardAcceptorIdentificationCode() {
		return cardAcceptorIdentificationCode;
	}
	public void setCardAcceptorIdentificationCode(
			String cardAcceptorIdentificationCode) {
		this.cardAcceptorIdentificationCode = cardAcceptorIdentificationCode;
	}
	public String getCardAcceptorName() {
		return cardAcceptorName;
	}
	public void setCardAcceptorName(String cardAcceptorName) {
		this.cardAcceptorName = cardAcceptorName;
	}
	public String getAdditionalResponseData() {
		return additionalResponseData;
	}
	public void setAdditionalResponseData(String additionalResponseData) {
		this.additionalResponseData = additionalResponseData;
	}
	public String getTrack1Data() {
		return track1Data;
	}
	public void setTrack1Data(String track1Data) {
		this.track1Data = track1Data;
	}
	public String getAdditionalDataIso() {
		return additionalDataIso;
	}
	public void setAdditionalDataIso(String additionalDataIso) {
		this.additionalDataIso = additionalDataIso;
	}
	public String getAdditionalDataNational() {
		return additionalDataNational;
	}
	public void setAdditionalDataNational(String additionalDataNational) {
		this.additionalDataNational = additionalDataNational;
	}
	public String getAdditionalDataPrivate() {
		return additionalDataPrivate;
	}
	public void setAdditionalDataPrivate(String additionalDataPrivate) {
		this.additionalDataPrivate = additionalDataPrivate;
	}
	public String getCurrencyCodeTransaction() {
		return currencyCodeTransaction;
	}
	public void setCurrencyCodeTransaction(String currencyCodeTransaction) {
		this.currencyCodeTransaction = currencyCodeTransaction;
	}
	public String getCurrencyCodeSettlement() {
		return currencyCodeSettlement;
	}
	public void setCurrencyCodeSettlement(String currencyCodeSettlement) {
		this.currencyCodeSettlement = currencyCodeSettlement;
	}
	public String getCurrencyCodeCardholderBilling() {
		return currencyCodeCardholderBilling;
	}
	public void setCurrencyCodeCardholderBilling(
			String currencyCodeCardholderBilling) {
		this.currencyCodeCardholderBilling = currencyCodeCardholderBilling;
	}
	public String getPersonalIdentificationNumberData() {
		return personalIdentificationNumberData;
	}
	public void setPersonalIdentificationNumberData(
			String personalIdentificationNumberData) {
		this.personalIdentificationNumberData = personalIdentificationNumberData;
	}
	public String getSecurityRelatedControlInformation() {
		return securityRelatedControlInformation;
	}
	public void setSecurityRelatedControlInformation(
			String securityRelatedControlInformation) {
		this.securityRelatedControlInformation = securityRelatedControlInformation;
	}
	public String getAdditionalAmounts() {
		return additionalAmounts;
	}
	public void setAdditionalAmounts(String additionalAmounts) {
		this.additionalAmounts = additionalAmounts;
	}
	public String getReservedIso1() {
		return reservedIso1;
	}
	public void setReservedIso1(String reservedIso1) {
		this.reservedIso1 = reservedIso1;
	}
	public String getReservedIso2() {
		return reservedIso2;
	}
	public void setReservedIso2(String reservedIso2) {
		this.reservedIso2 = reservedIso2;
	}
	public String getReservedNational1() {
		return reservedNational1;
	}
	public void setReservedNational1(String reservedNational1) {
		this.reservedNational1 = reservedNational1;
	}
	public String getReservedNational2() {
		return reservedNational2;
	}
	public void setReservedNational2(String reservedNational2) {
		this.reservedNational2 = reservedNational2;
	}
	public String getReservedNational3() {
		return reservedNational3;
	}
	public void setReservedNational3(String reservedNational3) {
		this.reservedNational3 = reservedNational3;
	}
	public String getReasonCode() {
		return reasonCode;
	}
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	public String getReservedPrivate1() {
		return reservedPrivate1;
	}
	public void setReservedPrivate1(String reservedPrivate1) {
		this.reservedPrivate1 = reservedPrivate1;
	}
	public String getReservedPrivate2() {
		return reservedPrivate2;
	}
	public void setReservedPrivate2(String reservedPrivate2) {
		this.reservedPrivate2 = reservedPrivate2;
	}
	public String getReservedPrivate3() {
		return reservedPrivate3;
	}
	public void setReservedPrivate3(String reservedPrivate3) {
		this.reservedPrivate3 = reservedPrivate3;
	}
	public String getMessageAuthenticationCode() {
		return messageAuthenticationCode;
	}
	public void setMessageAuthenticationCode(String messageAuthenticationCode) {
		this.messageAuthenticationCode = messageAuthenticationCode;
	}
	public String getBitMapTertiary() {
		return bitMapTertiary;
	}
	public void setBitMapTertiary(String bitMapTertiary) {
		this.bitMapTertiary = bitMapTertiary;
	}
	public String getSettlementCode() {
		return settlementCode;
	}
	public void setSettlementCode(String settlementCode) {
		this.settlementCode = settlementCode;
	}
	public String getExtendedPaymentCode() {
		return extendedPaymentCode;
	}
	public void setExtendedPaymentCode(String extendedPaymentCode) {
		this.extendedPaymentCode = extendedPaymentCode;
	}
	public String getReceivingInstitutionCountryCode() {
		return receivingInstitutionCountryCode;
	}
	public void setReceivingInstitutionCountryCode(
			String receivingInstitutionCountryCode) {
		this.receivingInstitutionCountryCode = receivingInstitutionCountryCode;
	}
	public String getSettlementInstitutionCountyCode() {
		return settlementInstitutionCountyCode;
	}
	public void setSettlementInstitutionCountyCode(
			String settlementInstitutionCountyCode) {
		this.settlementInstitutionCountyCode = settlementInstitutionCountyCode;
	}
	public String getNetworkManagementInformationCode() {
		return networkManagementInformationCode;
	}
	public void setNetworkManagementInformationCode(
			String networkManagementInformationCode) {
		this.networkManagementInformationCode = networkManagementInformationCode;
	}
	public String getMessageNumber() {
		return messageNumber;
	}
	public void setMessageNumber(String messageNumber) {
		this.messageNumber = messageNumber;
	}
	public String getMessageNumberLast() {
		return messageNumberLast;
	}
	public void setMessageNumberLast(String messageNumberLast) {
		this.messageNumberLast = messageNumberLast;
	}
	public String getDateAction() {
		return dateAction;
	}
	public void setDateAction(String dateAction) {
		this.dateAction = dateAction;
	}
	public String getCreditsNumber() {
		return creditsNumber;
	}
	public void setCreditsNumber(String creditsNumber) {
		this.creditsNumber = creditsNumber;
	}
	public String getCreditsReversalNumber() {
		return creditsReversalNumber;
	}
	public void setCreditsReversalNumber(String creditsReversalNumber) {
		this.creditsReversalNumber = creditsReversalNumber;
	}
	public String getDebitsNumber() {
		return debitsNumber;
	}
	public void setDebitsNumber(String debitsNumber) {
		this.debitsNumber = debitsNumber;
	}
	public String getDebitsReversalNumber() {
		return debitsReversalNumber;
	}
	public void setDebitsReversalNumber(String debitsReversalNumber) {
		this.debitsReversalNumber = debitsReversalNumber;
	}
	public String getTransferNumber() {
		return transferNumber;
	}
	public void setTransferNumber(String transferNumber) {
		this.transferNumber = transferNumber;
	}
	public String getTransferReversalNumber() {
		return transferReversalNumber;
	}
	public void setTransferReversalNumber(String transferReversalNumber) {
		this.transferReversalNumber = transferReversalNumber;
	}
	public String getInquiriesNumber() {
		return inquiriesNumber;
	}
	public void setInquiriesNumber(String inquiriesNumber) {
		this.inquiriesNumber = inquiriesNumber;
	}
	public String getAuthorisationsNumber() {
		return authorisationsNumber;
	}
	public void setAuthorisationsNumber(String authorisationsNumber) {
		this.authorisationsNumber = authorisationsNumber;
	}
	public String getCreditsProcesssingFeeAmount() {
		return creditsProcesssingFeeAmount;
	}
	public void setCreditsProcesssingFeeAmount(String creditsProcesssingFeeAmount) {
		this.creditsProcesssingFeeAmount = creditsProcesssingFeeAmount;
	}
	public String getCreditsTransactionFeeAmount() {
		return creditsTransactionFeeAmount;
	}
	public void setCreditsTransactionFeeAmount(String creditsTransactionFeeAmount) {
		this.creditsTransactionFeeAmount = creditsTransactionFeeAmount;
	}
	public String getDebitsProcessingFeeAmount() {
		return debitsProcessingFeeAmount;
	}
	public void setDebitsProcessingFeeAmount(String debitsProcessingFeeAmount) {
		this.debitsProcessingFeeAmount = debitsProcessingFeeAmount;
	}
	public String getDebitsTransactionFeeAmount() {
		return debitsTransactionFeeAmount;
	}
	public void setDebitsTransactionFeeAmount(String debitsTransactionFeeAmount) {
		this.debitsTransactionFeeAmount = debitsTransactionFeeAmount;
	}
	public String getCreditsAmount() {
		return creditsAmount;
	}
	public void setCreditsAmount(String creditsAmount) {
		this.creditsAmount = creditsAmount;
	}
	public String getCreditsReversalAmount() {
		return creditsReversalAmount;
	}
	public void setCreditsReversalAmount(String creditsReversalAmount) {
		this.creditsReversalAmount = creditsReversalAmount;
	}
	public String getDebitsAmount() {
		return debitsAmount;
	}
	public void setDebitsAmount(String debitsAmount) {
		this.debitsAmount = debitsAmount;
	}
	public String getDebitsReversalAmount() {
		return debitsReversalAmount;
	}
	public void setDebitsReversalAmount(String debitsReversalAmount) {
		this.debitsReversalAmount = debitsReversalAmount;
	}
	public String getOriginalDataElements() {
		return originalDataElements;
	}
	public void setOriginalDataElements(String originalDataElements) {
		this.originalDataElements = originalDataElements;
	}
	public String getFileUpdateCode() {
		return fileUpdateCode;
	}
	public void setFileUpdateCode(String fileUpdateCode) {
		this.fileUpdateCode = fileUpdateCode;
	}
	public String getFileSecurityCode() {
		return fileSecurityCode;
	}
	public void setFileSecurityCode(String fileSecurityCode) {
		this.fileSecurityCode = fileSecurityCode;
	}
	public String getResponseIndicator() {
		return responseIndicator;
	}
	public void setResponseIndicator(String responseIndicator) {
		this.responseIndicator = responseIndicator;
	}
	public String getServiceIndicator() {
		return serviceIndicator;
	}
	public void setServiceIndicator(String serviceIndicator) {
		this.serviceIndicator = serviceIndicator;
	}
	public String getReplacementAmounts() {
		return replacementAmounts;
	}
	public void setReplacementAmounts(String replacementAmounts) {
		this.replacementAmounts = replacementAmounts;
	}
	public String getMessageSecurityCode() {
		return messageSecurityCode;
	}
	public void setMessageSecurityCode(String messageSecurityCode) {
		this.messageSecurityCode = messageSecurityCode;
	}
	public String getAmountNetSettlement() {
		return amountNetSettlement;
	}
	public void setAmountNetSettlement(String amountNetSettlement) {
		this.amountNetSettlement = amountNetSettlement;
	}
	public String getPayee() {
		return payee;
	}
	public void setPayee(String payee) {
		this.payee = payee;
	}
	public String getSettlementInstitutionIdentificationCode() {
		return settlementInstitutionIdentificationCode;
	}
	public void setSettlementInstitutionIdentificationCode(
			String settlementInstitutionIdentificationCode) {
		this.settlementInstitutionIdentificationCode = settlementInstitutionIdentificationCode;
	}
	public String getReceivingInstitutionIdentificationCode() {
		return receivingInstitutionIdentificationCode;
	}
	public void setReceivingInstitutionIdentificationCode(
			String receivingInstitutionIdentificationCode) {
		this.receivingInstitutionIdentificationCode = receivingInstitutionIdentificationCode;
	}
	public String getSlipNumber() {
		return slipNumber;
	}
	public void setSlipNumber(String slipNumber) {
		this.slipNumber = slipNumber;
	}
	public String getFromAccount() {
		return fromAccount;
	}
	public void setFromAccount(String fromAccount) {
		this.fromAccount = fromAccount;
	}
	public String getToAccount() {
		return toAccount;
	}
	public void setToAccount(String toAccount) {
		this.toAccount = toAccount;
	}
	public String getTransactionDescription() {
		return transactionDescription;
	}
	public void setTransactionDescription(String transactionDescription) {
		this.transactionDescription = transactionDescription;
	}
	public String getReservedForIsoUse1() {
		return reservedForIsoUse1;
	}
	public void setReservedForIsoUse1(String reservedForIsoUse1) {
		this.reservedForIsoUse1 = reservedForIsoUse1;
	}
	public String getReservedForIsoUse2() {
		return reservedForIsoUse2;
	}
	public void setReservedForIsoUse2(String reservedForIsoUse2) {
		this.reservedForIsoUse2 = reservedForIsoUse2;
	}
	public String getReservedForIsoUse3() {
		return reservedForIsoUse3;
	}
	public void setReservedForIsoUse3(String reservedForIsoUse3) {
		this.reservedForIsoUse3 = reservedForIsoUse3;
	}
	public String getReservedForIsoUse4() {
		return reservedForIsoUse4;
	}
	public void setReservedForIsoUse4(String reservedForIsoUse4) {
		this.reservedForIsoUse4 = reservedForIsoUse4;
	}
	public String getReservedForIsoUse5() {
		return reservedForIsoUse5;
	}
	public void setReservedForIsoUse5(String reservedForIsoUse5) {
		this.reservedForIsoUse5 = reservedForIsoUse5;
	}
	public String getReservedForIsoUse6() {
		return reservedForIsoUse6;
	}
	public void setReservedForIsoUse6(String reservedForIsoUse6) {
		this.reservedForIsoUse6 = reservedForIsoUse6;
	}
	public String getReservedForIsoUse7() {
		return reservedForIsoUse7;
	}
	public void setReservedForIsoUse7(String reservedForIsoUse7) {
		this.reservedForIsoUse7 = reservedForIsoUse7;
	}
	public String getReservedForNationalUse() {
		return reservedForNationalUse;
	}
	public void setReservedForNationalUse(String reservedForNationalUse) {
		this.reservedForNationalUse = reservedForNationalUse;
	}
	public String getAuthorisingAgentInstitutionIdCode() {
		return authorisingAgentInstitutionIdCode;
	}
	public void setAuthorisingAgentInstitutionIdCode(
			String authorisingAgentInstitutionIdCode) {
		this.authorisingAgentInstitutionIdCode = authorisingAgentInstitutionIdCode;
	}
	public String getReservedForNationalUse1() {
		return reservedForNationalUse1;
	}
	public void setReservedForNationalUse1(String reservedForNationalUse1) {
		this.reservedForNationalUse1 = reservedForNationalUse1;
	}
	public String getReservedForNationalUse2() {
		return reservedForNationalUse2;
	}
	public void setReservedForNationalUse2(String reservedForNationalUse2) {
		this.reservedForNationalUse2 = reservedForNationalUse2;
	}
	public String getReservedForNationalUse3() {
		return reservedForNationalUse3;
	}
	public void setReservedForNationalUse3(String reservedForNationalUse3) {
		this.reservedForNationalUse3 = reservedForNationalUse3;
	}
	public String getReservedForNationalUse4() {
		return reservedForNationalUse4;
	}
	public void setReservedForNationalUse4(String reservedForNationalUse4) {
		this.reservedForNationalUse4 = reservedForNationalUse4;
	}
	public String getReservedForNationalUse5() {
		return reservedForNationalUse5;
	}
	public void setReservedForNationalUse5(String reservedForNationalUse5) {
		this.reservedForNationalUse5 = reservedForNationalUse5;
	}
	public String getReservedForNationalUse6() {
		return reservedForNationalUse6;
	}
	public void setReservedForNationalUse6(String reservedForNationalUse6) {
		this.reservedForNationalUse6 = reservedForNationalUse6;
	}
	public String getReservedForPrivateUse1() {
		return reservedForPrivateUse1;
	}
	public void setReservedForPrivateUse1(String reservedForPrivateUse1) {
		this.reservedForPrivateUse1 = reservedForPrivateUse1;
	}
	public String getReservedForPrivateUse2() {
		return reservedForPrivateUse2;
	}
	public void setReservedForPrivateUse2(String reservedForPrivateUse2) {
		this.reservedForPrivateUse2 = reservedForPrivateUse2;
	}
	public String getReservedForPrivateUse3() {
		return reservedForPrivateUse3;
	}
	public void setReservedForPrivateUse3(String reservedForPrivateUse3) {
		this.reservedForPrivateUse3 = reservedForPrivateUse3;
	}
	public String getReservedForPrivateUse4() {
		return reservedForPrivateUse4;
	}
	public void setReservedForPrivateUse4(String reservedForPrivateUse4) {
		this.reservedForPrivateUse4 = reservedForPrivateUse4;
	}
	public String getInfoText() {
		return infoText;
	}
	public void setInfoText(String infoText) {
		this.infoText = infoText;
	}
	public String getNetworkManagementInformation() {
		return networkManagementInformation;
	}
	public void setNetworkManagementInformation(String networkManagementInformation) {
		this.networkManagementInformation = networkManagementInformation;
	}
	public String getIssuerTraceId() {
		return issuerTraceId;
	}
	public void setIssuerTraceId(String issuerTraceId) {
		this.issuerTraceId = issuerTraceId;
	}
	public String getReservedForPrivateUse() {
		return reservedForPrivateUse;
	}
	public void setReservedForPrivateUse(String reservedForPrivateUse) {
		this.reservedForPrivateUse = reservedForPrivateUse;
	}
	public String getMessageAuthenticationCode2() {
		return messageAuthenticationCode2;
	}
	public void setMessageAuthenticationCode2(String messageAuthenticationCode2) {
		this.messageAuthenticationCode2 = messageAuthenticationCode2;
	}
	
	@Override
	public String toString() {
		return U.dump(this);
	}

}
