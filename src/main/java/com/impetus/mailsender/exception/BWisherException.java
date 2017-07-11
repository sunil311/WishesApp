package com.impetus.mailsender.exception;

import java.io.IOException;
import java.util.Properties;

import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class BWisherException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    private static Properties bdProperties;
    static {
        bdProperties = new Properties();
        try {
            bdProperties.load(BWisherException.class.getClassLoader().getResourceAsStream("mailsender-custom.properties"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public BWisherException() {
        super();
        this.sendFailureNotification("", null);
    }

    public BWisherException(String msg) {
        super(msg);
        this.sendFailureNotification(msg, null);
    }

    public BWisherException(Throwable th) {
        super(th);
        this.sendFailureNotification("", th);
    }

    public BWisherException(String msg, Throwable th) {
        super(msg, th);
        this.sendFailureNotification(msg, th);
    }

    public void sendFailureNotification(String msg, Throwable th) {
        MimeMessagePreparator messagePreparator = getMessagePreparator(msg, th);
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setDefaultEncoding("UTF-8"); // Using mail from impetus.
        // mailSender.setHost("server-020.impetus.co.in");
        mailSender.setHost(bdProperties.getProperty("mail.smtp.host"));
        mailSender.setPort(Integer.parseInt(bdProperties.getProperty("mail.smtp.port")));
        mailSender.setUsername(bdProperties.getProperty("mail.smtp.username"));
        mailSender.setPassword(bdProperties.getProperty("mail.smtp.password"));

        Properties javaMailProperties = new Properties();
        javaMailProperties.put("mail.smtp.auth", bdProperties.getProperty("mail.smtp.auth"));
        javaMailProperties.put("mail.transport.protocol", bdProperties.getProperty("mail.transport.protocol"));
        javaMailProperties.put("mail.debug", bdProperties.getProperty("mail.debug"));

        /*
         * mailSender.setHost("smtp.gmail.com"); mailSender.setPort(587); mailSender.setUsername("sunil.mact@gmail.com");
         * mailSender.setPassword("sunil@311"); Properties javaMailProperties = new Properties(); javaMailProperties.put("mail.smtp.auth", "true");
         * javaMailProperties.put("mail.transport.protocol", "smtp"); javaMailProperties.put("mail.debug", "true");
         * javaMailProperties.put("mail.smtp.starttls.enable", "true");
         */
        javaMailProperties.put("mail.smtp.starttls.enable", "true");

        mailSender.send(messagePreparator);
    }

    public MimeMessagePreparator getMessagePreparator(final String msg, Throwable th) {

        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_RELATED, "UTF-8");
                messageHelper.setFrom("sparkbd@impetus.co.in");
                // messageHelper.setTo("sunil.gupta@impetus.co.in");
                messageHelper.setTo(bdProperties.getProperty("exception.reciever"));
                if (!StringUtils.isBlank(msg) && th != null) {
                    messageHelper.setText(msg + th.getMessage(), true);
                    messageHelper.setSubject(msg + th.getMessage());
                } else if (!StringUtils.isBlank(msg) && th == null) {
                    messageHelper.setText(msg, true);
                    messageHelper.setSubject(msg);
                } else if (StringUtils.isBlank(msg) && th != null) {
                    messageHelper.setText(th.getMessage(), true);
                    messageHelper.setSubject(th.getMessage());
                } else {
                    messageHelper.setText("Email sending failed.", true);
                    messageHelper.setSubject("Email sending failed.");
                }
            }
        };
        return preparator;
    }

}
