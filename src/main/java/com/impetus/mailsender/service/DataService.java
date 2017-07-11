package com.impetus.mailsender.service;

import java.util.Date;
import java.util.List;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.beans.Filter;

public interface DataService {
    /** @param filter
     * @param mailDate
     * @return */
    public List<Employee> getEmployees(Filter filter, Date mailDate);
}
