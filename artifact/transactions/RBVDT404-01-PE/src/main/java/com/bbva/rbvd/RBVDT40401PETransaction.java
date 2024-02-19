package com.bbva.rbvd;

import com.bbva.rbvd.lib.r407.RBVDR407;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dto.GetQuotationDTO;
import com.bbva.elara.domain.transaction.RequestHeaderParamsName;
import com.bbva.elara.domain.transaction.Severity;
import com.bbva.elara.domain.transaction.response.HttpResponseCode;
import com.bbva.rbvd.dto.enterpriseinsurance.createquotation.dto.CreateQuotationDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

import static java.util.Objects.nonNull;

/**
 * getquotation
 *
 */
public class
RBVDT40401PETransaction extends AbstractRBVDT40401PETransaction {

	private static final Logger LOGGER = LoggerFactory.getLogger(RBVDT40401PETransaction.class);

	/**
	 * The execute method...
	 */
	@Override
	public void execute() {

		LOGGER.info("RBVDT40201PETransaction - Start");
		LOGGER.info("Header traceId: {}", this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.REQUESTID));
		String channelCode = (String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.CHANNELCODE);
		String userAudit = (String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.USERCODE);
		String creationUser = (String) this.getRequestHeader().getHeaderParameter(RequestHeaderParamsName.USERCODE);
		String quotationId = this.getQuotationid();
		RBVDR407 rbvdR407 = this.getServiceLibrary(RBVDR407.class);
		GetQuotationDTO response = rbvdR407.executeGetQuotation(quotationId);

		if(nonNull(response)) {
			LOGGER.info("RBVDT40201PETransaction - Response : {}",response.toString());
			LOGGER.info("RBVDT40201PETransaction - product: {}",response.getProduct());
			LOGGER.info("RBVDT40201PETransaction - quotation reference : {}",response.getQuotationReference());
			LOGGER.info("RBVDT40201PETransaction - contactdetail : {}",response.getContactDetailsDTO());
			this.setProduct(response.getProduct());
			this.setParticipants(response.getParticipantDTO());
			this.setEmployees(response.getEmployees());
			this.setStatus(response.getStatus());
			this.setId(response.getId());
			this.setBusinessagent(response.getBusinessAgent());
			this.setContactdetails(response.getContactDetailsDTO());
			this.setValidityperiod(response.getValidityPeriodDTO());
			this.setQuotationreference(response.getQuotationReference());
			this.setQuotationdate(new Date());
			this.setHttpResponseCode(HttpResponseCode.HTTP_CODE_200, Severity.OK);
		} else {
			this.setSeverity(Severity.ENR);
		}


	}


}
