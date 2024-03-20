package com.bbva.rbvd.lib.r407.impl.business.impl;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.AmountDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EmployeesDTO;
import com.bbva.rbvd.dto.enterpriseinsurance.utils.ConstantsUtil;
import com.bbva.rbvd.lib.r407.impl.business.IEmployeesBusiness;
import com.bbva.rbvd.lib.r407.impl.utils.ConvertUtils;
import com.bbva.rbvd.lib.r407.impl.utils.ValidateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Map;

public class EmployeesBusinessImpl implements IEmployeesBusiness {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmployeesBusinessImpl.class);


    @Override
    public EmployeesDTO constructEmployeesInfo(Map<String, Object> employeeInfo) {
        if(!ValidateUtils.mapIsNullOrEmpty(employeeInfo) && ValidateUtils.mapNotContainsNullValue(employeeInfo)){
            LOGGER.info("EmployeesBusinessImpl - constructEmployeesInResponseTrx() - employeeInfo not null");

            EmployeesDTO employees = new EmployeesDTO();

            String areMajority = (String) employeeInfo.get(ConstantsUtil.InsuranceQuoteCoLife.FIELD_AGE_EMPLOYEES_IND_TYPE);
            employees.setAreMajorityAge(areMajority.equals("1"));

            BigDecimal employeesNumber = ConvertUtils.getBigDecimalValue(employeeInfo.get(
                    ConstantsUtil.InsuranceQuoteCoLife.FIELD_PAYROLL_EMPLOYEE_NUMBER));
            employees.setEmployeesNumber(employeesNumber.longValue());

            AmountDTO payrollAmount = new AmountDTO();
            BigDecimal amount = ConvertUtils.getBigDecimalValue(employeeInfo.get(
                    ConstantsUtil.InsuranceQuoteCoLife.FIELD_INCOMES_PAYROLL_AMOUNT));
            payrollAmount.setAmount(amount.doubleValue());
            payrollAmount.setCurrency((String) employeeInfo.get(ConstantsUtil.InsuranceQuoteCoLife.FIELD_CURRENCY_ID));
            employees.setMonthlyPayrollAmount(payrollAmount);

            LOGGER.info("EmployeesBusinessImpl - constructEmployeesInResponseTrx() - employees {}",
                    ConvertUtils.getRequestJsonFormat(employees));

            return employees;
        }
        return null;
    }
}
