package com.bbva.rbvd;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EnterpriseQuotationDTO;
import com.bbva.rbvd.lib.r407.RBVDR407;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.sql.Date;
import java.time.Instant;
import java.time.ZoneId;

import static java.util.Objects.nonNull;

/**
 * getquotation
 *
 */
public class RBVDT40401PETransaction extends AbstractRBVDT40401PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDT40401PETransaction.class);

	@Override
	public void execute() {

		LOGGER.info("RBVDT40401PETransaction - START | execute()");

		RBVDR407 rbvdR407 = this.getServiceLibrary(RBVDR407.class);

		String quotationId = this.getQuotationid();
		String traceId = (String) this.getContext().getTransactionRequest().getHeader().getHeaderParameter(
				RequestHeaderParamsName.REQUESTID);
		String transactionCode = (String) this.getContext().getTransactionRequest().getHeader().getHeaderParameter(
				RequestHeaderParamsName.LOGICALTRANSACTIONCODE);

		EnterpriseQuotationDTO response = rbvdR407.executeGetQuotationLogic(quotationId,traceId,transactionCode);

		if(nonNull(response)) {
			LOGGER.info("RBVDT40401PETransaction - Response : {}",response);

			this.setId(response.getId());
			this.setQuotationdate(Date.from(Instant.from(response.getQuotationDate().atStartOfDay(ZoneId.of("GMT")))));
			this.setEmployees(response.getEmployees());
			this.setProduct(response.getProduct());
			this.setContactdetails(response.getContactDetails());
			this.setValidityperiod(response.getValidityPeriod());
			this.setBusinessagent(response.getBusinessAgent());
			this.setParticipants(response.getParticipants());
			this.setQuotationreference(response.getQuotationReference());
			this.setStatus(response.getStatus());
			this.setPaymentmethod(response.getPaymentMethod());
			this.setBank(response.getBank());

			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_200, Severity.OK);
		} else {
			this.setSeverity(Severity.ENR);
		}

	}


}
