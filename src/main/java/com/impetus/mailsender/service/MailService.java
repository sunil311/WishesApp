package com.impetus.mailsender.service;

import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.configuration.PropertiesUtil;
import com.impetus.mailsender.exception.BWisherException;
import com.impetus.mailsender.util.EmailHelper;

@Service
public class MailService {

    private static Logger logger = LoggerFactory.getLogger(MailService.class);
    @Autowired
    PropertiesUtil propertiesUtil;

    @Value("${mail.smtp.host}")
    private String smtpHost;

    @Value("${mail.smtp.port}")
    private int smtpPort;

    @Value("${mail.smtp.username}")
    private String smtpUserName;

    @Value("${mail.smtp.password}")
    private String smtpPassword;

    @Value("${mail.smtp.auth}")
    private String smtpAuth;

    @Value("${mail.transport.protocol}")
    private String smtpProtocol;

    @Value("${mail.debug}")
    private String smtpDebug;

    /** @param employee */
    public void sendEmail(Employee employee) {
        try {
            EmailHelper.prepareMailContent(employee, propertiesUtil);
            Session session = buildMailSession();
            Message withImage = EmailHelper.buildMessageWithEmbeddedImage(session,
                    "ov" + employee.getIMGURL().substring(employee.getIMGURL().lastIndexOf("/") + 1), employee, propertiesUtil);
            addressAndSendMessage(withImage, employee.getEMAIL());
        } catch (Exception ex) {
            throw new BWisherException("Email Sending failed.", ex);
        }

    }

    /** @return */
    public Session buildMailSession() {
        Properties mailProps = new Properties();
        mailProps.put("mail.transport.protocol", smtpProtocol);
        mailProps.put("mail.host", smtpHost);
        mailProps.put("mail.from", "sparkbd@impetus.co.in");
        // mailProps.put("mail.smtp.starttls.enable", "true");
        mailProps.put("mail.smtp.port", smtpPort);
        mailProps.put("mail.smtp.auth", smtpAuth);
        // final, because we're using it in the closure below
        // Add ur email id and pass
        final PasswordAuthentication usernamePassword = new PasswordAuthentication(smtpUserName, smtpPassword);
        Authenticator auth = new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return usernamePassword;
            }
        };
        Session session = Session.getInstance(mailProps, auth);
        session.setDebug(true);
        return session;
    }

    /** @param message
     * @param recipient
     * @throws AddressException
     * @throws MessagingException */
    public void addressAndSendMessage(Message message, String recipient) throws AddressException, MessagingException {
        message.setRecipient(RecipientType.TO, new InternetAddress(recipient));
        Transport.send(message);
    }
}
