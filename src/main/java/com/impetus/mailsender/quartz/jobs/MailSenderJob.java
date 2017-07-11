package com.impetus.mailsender.quartz.jobs;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.beans.Filter;
import com.impetus.mailsender.configuration.PropertiesUtil;
import com.impetus.mailsender.exception.BWisherException;
import com.impetus.mailsender.service.EmployeeService;
import com.impetus.mailsender.service.MailService;
import com.impetus.mailsender.util.EmailHelper;

@Component
public class MailSenderJob implements Job {

    @Value("${previous.mail.sent.days}")
    private int previousMailsDay;

    @Autowired
    private MailService mailService;

    @Autowired
    PropertiesUtil propertiesUtil;

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    Filter filter;

    private final static Logger logger = LoggerFactory.getLogger(MailSenderJob.class);
    @Value("${cron.frequency.jobwithcrontrigger}")
    private String frequency;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            if (employeeService != null && employeeService.getDataService() != null) {
                int mailCounter = EmailHelper.getMailCounter(previousMailsDay);
                for (int counter = mailCounter - 1; counter >= 0; counter--) {
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DATE, -counter);
                    Date mailDate = c.getTime();
                    sendMail(counter, mailDate);
                }
            } else {
                throw new BWisherException("Email sending failed. EmployeeService is null. Please set EmployeeService first and try again.");
            }
        } catch (IOException | ParseException ex) {
            throw new BWisherException("Email sending failed.", ex);
        }
        logger.info("Running MailSenderJob | frequency {}", frequency);
    }

    /** @param counter
     * @param mailDate
     * @throws ParseException
     * @throws FileNotFoundException
     * @throws IOException */
    private void sendMail(int counter, Date mailDate) throws ParseException, FileNotFoundException, IOException {
        if (!EmailHelper.checkMailStatus(mailDate)) {
            List<Employee> employees = employeeService.getDataService().getEmployees(filter, mailDate);
            if (employees != null && !employees.isEmpty()) {
                for (Employee employee : employees) {
                    if (employee.getSUBJECT().equalsIgnoreCase("Birthday")) {
                        if (counter != 0) {
                            employee.setSUBJECT("Belated Happy " + employee.getSUBJECT());
                        } else {
                            employee.setSUBJECT("Happy " + employee.getSUBJECT());
                        }
                        mailService.sendEmail(employee);
                    }
                }
                // Updating flag file
                EmailHelper.updateFlagFile(mailDate);
            }
        }
    }

    public int getPreviousMailsDay() {
        return previousMailsDay;
    }

    public void setPreviousMailsDay(int previousMailsDay) {
        this.previousMailsDay = previousMailsDay;
    }
}
