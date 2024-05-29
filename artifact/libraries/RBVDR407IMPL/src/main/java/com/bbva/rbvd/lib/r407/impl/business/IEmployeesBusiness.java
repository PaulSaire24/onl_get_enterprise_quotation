package com.bbva.rbvd.lib.r407.impl.business;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EmployeesDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.QuotationDAO;

public interface IEmployeesBusiness {

    EmployeesDTO constructEmployees(QuotationDAO responseQuotation);

}
