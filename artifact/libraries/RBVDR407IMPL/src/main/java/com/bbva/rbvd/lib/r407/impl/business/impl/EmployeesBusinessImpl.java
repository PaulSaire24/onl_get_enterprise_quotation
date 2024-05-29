package com.bbva.rbvd.lib.r407.impl.business.impl;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.AmountDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EmployeesDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.getquotation.dao.QuotationDAO;
import com.bbva.rbvd.lib.r407.impl.business.IEmployeesBusiness;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class EmployeesBusinessImpl implements IEmployeesBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeesBusinessImpl.class);


    @Override
    public EmployeesDTO constructEmployees(QuotationDAO responseQuotation) {
        if(ValidateUtils.allValuesNotNullOrEmpty(Arrays.asList(responseQuotation.getPayrollAmount(),
        responseQuotation.getPayrollCurrencyId(),responseQuotation.getEmployeeNumber(),responseQuotation.getEmployeesIndType()))){
            LOGGER.info("EmployeesBusinessImpl - constructEmployees() - employees Info not null");

            EmployeesDTO employees = new EmployeesDTO();

            employees.setAreMajorityAge(responseQuotation.getEmployeesIndType().equals("1"));
            employees.setEmployeesNumber(responseQuotation.getEmployeeNumber().longValue());

            AmountDTO payrollAmount = new AmountDTO();
            payrollAmount.setAmount(responseQuotation.getPayrollAmount().doubleValue());
            payrollAmount.setCurrency(responseQuotation.getPayrollCurrencyId());
            employees.setMonthlyPayrollAmount(payrollAmount);

            LOGGER.info("EmployeesBusinessImpl - constructEmployeesInResponseTrx() - employees {}", employees);

            return employees;
        }
        return null;
    }
}
