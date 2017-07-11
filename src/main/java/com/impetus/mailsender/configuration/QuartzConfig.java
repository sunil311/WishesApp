package com.impetus.mailsender.configuration;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.sql.DataSource;

import org.quartz.JobDetail;
import org.quartz.SimpleTrigger;
import org.quartz.Trigger;
import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;
import org.springframework.scheduling.quartz.JobDetailFactoryBean;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.impetus.mailsender.quartz.jobs.LoadEmployeeJob;
import com.impetus.mailsender.quartz.jobs.MailSenderJob;

@Configuration
@ConditionalOnProperty(name = "quartz.enabled")
public class QuartzConfig {
    @Value("${cron.frequency.jobwithcrontrigger}")
    private String frequency;

    @Value("${cron.frequency.dataLoadFrequency}")
    private String dataLoadFrequency;

    @Autowired
    List<Trigger> triggers;

    /** @param applicationContext
     * @return */
    @Bean
    public JobFactory jobFactory(ApplicationContext applicationContext) {
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    /** @param dataSource
     * @param jobFactory
     * @return
     * @throws IOException */
    @Bean
    public SchedulerFactoryBean schedulerFactoryBean(DataSource dataSource, JobFactory jobFactory) throws IOException {
        SchedulerFactoryBean schedulerFactory = new SchedulerFactoryBean();
        schedulerFactory.setOverwriteExistingJobs(true);
        schedulerFactory.setAutoStartup(true);
        schedulerFactory.setDataSource(dataSource);
        schedulerFactory.setJobFactory(jobFactory);
        schedulerFactory.setQuartzProperties(quartzProperties());

        if (!triggers.isEmpty()) {
            schedulerFactory.setTriggers(triggers.toArray(new Trigger[triggers.size()]));
        }

        return schedulerFactory;
    }

    /** @return
     * @throws IOException */
    @Bean
    public Properties quartzProperties() throws IOException {
        PropertiesFactoryBean propertiesFactoryBean = new PropertiesFactoryBean();
        propertiesFactoryBean.setLocation(new ClassPathResource("/quartz.properties"));
        propertiesFactoryBean.afterPropertiesSet();
        return propertiesFactoryBean.getObject();
    }

    /** @param jobDetail
     * @param cronExpression
     * @return */
    public static CronTriggerFactoryBean createCronTrigger(JobDetail jobDetail, String cronExpression) {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(jobDetail);
        factoryBean.setCronExpression(cronExpression);
        factoryBean.setMisfireInstruction(SimpleTrigger.MISFIRE_INSTRUCTION_FIRE_NOW);
        return factoryBean;
    }

    /** @param jobClass
     * @return */
    @SuppressWarnings("rawtypes")
    public static JobDetailFactoryBean createJobDetail(Class jobClass) {
        JobDetailFactoryBean factoryBean = new JobDetailFactoryBean();
        factoryBean.setJobClass(jobClass);
        // job has to be durable to be stored in DB:
        factoryBean.setDurability(true);
        return factoryBean;
    }

    @Bean(name = "jobWithCronTriggerBean")
    public JobDetailFactoryBean sampleJob() {
        return QuartzConfig.createJobDetail(MailSenderJob.class);
    }

    @Bean(name = "jobWithCronTriggerBeanTrigger")
    public CronTriggerFactoryBean sampleJobTrigger(@Qualifier("jobWithCronTriggerBean") JobDetail jobDetail) {
        return QuartzConfig.createCronTrigger(jobDetail, frequency);
    }

    @Bean(name = "dataLoadJob")
    public JobDetailFactoryBean dataLoadJob() {
        return QuartzConfig.createJobDetail(LoadEmployeeJob.class);
    }

    @Bean(name = "dataLoadJobTrigger")
    public CronTriggerFactoryBean dataLoadJobTrigger(@Qualifier("dataLoadJob") JobDetail jobDetail) {
        return QuartzConfig.createCronTrigger(jobDetail, dataLoadFrequency);
    }
}
