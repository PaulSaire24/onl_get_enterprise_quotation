package com.bbva.rbvd;

import com.bbva.elara.transaction.AbstractTransaction;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.BankDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ContactDetailsDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.DescriptionDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EmployeesDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ParticipantDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.PaymentMethodDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ProductDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.ValidityPeriodDTO;
import java.util.Date;
import java.util.List;

/**
 * In this class, the input and output data is defined automatically through the setters and getters.
 */
public abstract class AbstractRBVDT40401PETransaction extends AbstractTransaction {

	public AbstractRBVDT40401PETransaction(){
	}


	/**
	 * Return value for input parameter quotationId
	 */
	protected String getQuotationid(){
		return (String)this.getParameter("quotationId");
	}

	/**
	 * Set value for String output parameter id
	 */
	protected void setId(final String field){
		this.addParameter("id", field);
	}

	/**
	 * Set value for Date output parameter quotationDate
	 */
	protected void setQuotationdate(final Date field){
		this.addParameter("quotationDate", field);
	}

	/**
	 * Set value for EmployeesDTO output parameter employees
	 */
	protected void setEmployees(final EmployeesDTO field){
		this.addParameter("employees", field);
	}

	/**
	 * Set value for ProductDTO output parameter product
	 */
	protected void setProduct(final ProductDTO field){
		this.addParameter("product", field);
	}

	/**
	 * Set value for List<ContactDetailsDTO> output parameter contactDetails
	 */
	protected void setContactdetails(final List<ContactDetailsDTO> field){
		this.addParameter("contactDetails", field);
	}

	/**
	 * Set value for ValidityPeriodDTO output parameter validityPeriod
	 */
	protected void setValidityperiod(final ValidityPeriodDTO field){
		this.addParameter("validityPeriod", field);
	}

	/**
	 * Set value for DescriptionDTO output parameter businessAgent
	 */
	protected void setBusinessagent(final DescriptionDTO field){
		this.addParameter("businessAgent", field);
	}

	/**
	 * Set value for List<ParticipantDTO> output parameter participants
	 */
	protected void setParticipants(final List<ParticipantDTO> field){
		this.addParameter("participants", field);
	}

	/**
	 * Set value for String output parameter quotationReference
	 */
	protected void setQuotationreference(final String field){
		this.addParameter("quotationReference", field);
	}

	/**
	 * Set value for DescriptionDTO output parameter status
	 */
	protected void setStatus(final DescriptionDTO field){
		this.addParameter("status", field);
	}

	/**
	 * Set value for PaymentMethodDTO output parameter paymentMethod
	 */
	protected void setPaymentmethod(final PaymentMethodDTO field){
		this.addParameter("paymentMethod", field);
	}

	/**
	 * Set value for BankDTO output parameter bank
	 */
	protected void setBank(final BankDTO field){
		this.addParameter("bank", field);
	}
}
