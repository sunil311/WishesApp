package com.impetus.mailsender.quartz.jobs;

import java.text.ParseException;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.exception.BWisherException;
import com.impetus.mailsender.service.DBService;
import com.impetus.mailsender.util.DataHelper;

@Component
public class LoadEmployeeJob implements Job {
    @Autowired
    private DBService dbService;

    private final static Logger logger = LoggerFactory.getLogger(LoadEmployeeJob.class);

    @Value("${cron.frequency.dataLoadFrequency}")
    private String dataLoadFrequency;
    @Value("${employee.csvDataLocation}")
    private String csvDataLocation;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        logger.info("Running LoadEmployeeJob | frequency {}", dataLoadFrequency);
        List<Employee> employees;
        try {
            employees = DataHelper.readEmployeesFromCSV(csvDataLocation);
            dbService.loadEmployees(employees);
            logger.info("Database updated...");
        } catch (ParseException e) {
            throw new BWisherException(e.getMessage());
        }
    }
}
