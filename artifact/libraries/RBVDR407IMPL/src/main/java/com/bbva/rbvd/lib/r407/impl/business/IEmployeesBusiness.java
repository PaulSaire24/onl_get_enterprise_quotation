package com.bbva.rbvd.lib.r407.impl.business;

import com.bbva.rbvd.dto.enterpriseinsurance.commons.dto.EmployeesDTO;

import java.util.Map;

public interface IEmployeesBusiness {

    EmployeesDTO constructEmployees(Map<String,Object> employeeInfo);

}
