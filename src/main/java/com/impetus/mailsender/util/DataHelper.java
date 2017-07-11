package com.impetus.mailsender.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.beans.Filter;

public class DataHelper {

    /** @param csvPath
     * @return
     * @throws ParseException */
    public static List<Employee> readEmployeesFromCSV(String csvPath) throws ParseException {
        List<Employee> employees = new ArrayList<>();
        Scanner scanner = null;
        try {
            scanner = new Scanner(new FileInputStream(new File(csvPath)));
            int counter = 0;
            while (scanner.hasNextLine()) {
                counter = counter + 1;
                String line = scanner.nextLine();
                if (counter != 1) {
                    // Employee ID,Name,Client Dept.,Project,DOJ,Birthday,Anniversary,Email,Location
                    String[] columns = line.split(",");
                    Employee employee = new Employee();
                    employee.setEmpId(columns[0]);
                    employee.setNAME(columns[1]);
                    employee.setClient(columns[2]);
                    employee.setProject(columns[3]);
                    employee.setDoj(formateDate(columns[4]));
                    employee.setbDay(formateDate(columns[5]));
                    employee.setAnniversary(formateDate(columns[6]));
                    employee.setEMAIL(columns[7]);
                    employee.setLocation(columns[8]);
                    employees.add(employee);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
        return employees;
    }

    /** @param dateString
     * @return
     * @throws ParseException */
    public static Date formateDate(String dateString) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        if (StringUtils.isNotBlank(dateString)) {
            return dateFormat.parse(dateString.trim());
        } else {
            return null;
        }
    }

    /** @param employees
     * @param filter
     * @return */
    public static List<Employee> applyFilter(List<Employee> employees, Filter filter) {
        List<Employee> updatedEmployees = new ArrayList<>();
        try {
            for (Employee employee : employees) {
                Date ddMM = getDDMM(new Date());
                Date dob = getDDMM(employee.getbDay());
                employee = filterEmployee(filter, updatedEmployees, employee, ddMM, dob);
                if (employee != null) {
                    updatedEmployees.add(employee);
                }

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return updatedEmployees;
    }

    /** @param filter
     * @param updatedEmployees
     * @param employee
     * @param ddMM
     * @param dob
     * @param filterLocs
     * @return */
    private static Employee filterEmployee(Filter filter, List<Employee> updatedEmployees, Employee employee, Date ddMM, Date dob) {
        String filterClients[] = filter.getClients();
        String filterLocs[] = filter.getLocations();
        boolean locFlag = false;
        boolean clientFlag = false;
        boolean dobFlag = false;
        for (String loc : filterLocs) {
            if (loc.equalsIgnoreCase(employee.getLocation())) {
                locFlag = true;
                break;
            }
        }
        for (String client : filterClients) {
            if (client.equalsIgnoreCase(employee.getClient())) {
                clientFlag = true;
                break;
            }
        }

        if (dob != null && ddMM.compareTo(dob) == 0) {
            employee.setSUBJECT("Birthday");
            dobFlag = true;
        }
        if (locFlag && clientFlag && dobFlag) {
            return employee;
        } else
            return null;
    }

    /** @param date
     * @return
     * @throws ParseException */
    private static Date getDDMM(Date date) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("dd MM");
        if (date != null) {
            String dateString = dateFormat.format(date);
            return dateFormat.parse(dateString);
        }
        return null;
    }

    /** @param date
     * @return */
    public static String formateDateYYYYMMDD(Date date) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        return dateFormat.format(date);
    }
}
