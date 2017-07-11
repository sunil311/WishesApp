package com.impetus.mailsender.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.commons.lang3.SystemUtils;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;

import com.impetus.mailsender.beans.Employee;
import com.impetus.mailsender.configuration.PropertiesUtil;
import com.sun.mail.smtp.SMTPMessage;

@SuppressWarnings("deprecation")
public class EmailHelper {

    @Value("${previous.mail.sent.days}")
    private int previousMailSentDays;

    static Logger logger = LoggerFactory.getLogger(EmailHelper.class);

    /** @param employee
     * @throws FileNotFoundException
     * @throws IOException
     * @throws MessagingException
     * @throws URISyntaxException */
    public static void prepareMailContent(Employee employee, PropertiesUtil propertiesUtil) throws FileNotFoundException, IOException,
            MessagingException, URISyntaxException {
        String templatePath = propertiesUtil.getProperty("backgroundImagesPath");
        String selectedRandompath = EmailHelper.selectRandomBGImage(templatePath, propertiesUtil);
        String imagePath = getImagePath(employee, propertiesUtil);
        ImageOverlay.createOverlay(selectedRandompath, imagePath, "ov" + imagePath.substring(imagePath.lastIndexOf("/") + 1));
    }

    /** @param iamgeURL
     * @throws IOException
     * @throws FileNotFoundException */
    @SuppressWarnings("restriction")
    public static String writeImageToDisk(String iamgeURL, PropertiesUtil propertiesUtil, String imagePath) throws FileNotFoundException, IOException {
        logger.debug("iamgeURL :" + iamgeURL);
        // username:password
        String loginPassword = propertiesUtil.getProperty("pivotImageDownloadUser") + ":" + propertiesUtil.getProperty("pivotImageDownloadPassword");
        // String passwdstring = "sunil.gupta:mahi@29nov";
        String encoding = new sun.misc.BASE64Encoder().encode(loginPassword.getBytes());
        URL url = new URL(iamgeURL);
        URLConnection uc = url.openConnection();
        uc.setRequestProperty("Authorization", "Basic " + encoding);
        InputStream content = uc.getInputStream();
        OutputStream outputStream = new FileOutputStream(new File(imagePath));
        int read = 0;
        byte[] bytes = new byte[1024];
        while ((read = content.read(bytes)) != -1) {
            outputStream.write(bytes, 0, read);
        }
        logger.debug("write done");
        outputStream.close();
        return imagePath;
    }

    /** @param backgrounImagePath
     * @return
     * @throws MessagingException
     * @throws IOException
     * @throws FileNotFoundException */
    public static String selectRandomBGImage(String backgrounImagePath, PropertiesUtil propertiesUtil) throws MessagingException,
            FileNotFoundException, IOException {
        File file = new File(backgrounImagePath);
        File[] files = null;
        String bachgrounImage = "";

        if (file.isDirectory()) {
            files = file.listFiles();
            int index = RandomUtils.nextInt(files.length);
            if (files[index].isFile()) {
                bachgrounImage = backgrounImagePath + files[index].getName();
            } else {
                bachgrounImage = propertiesUtil.getProperty("defaultBackgroundImage");
            }
        } else {
            bachgrounImage = propertiesUtil.getProperty("defaultBackgroundImage");
        }
        logger.debug("template name :" + bachgrounImage);
        return bachgrounImage;
    }

    /** @param employee
     * @param propertiesUtil
     * @return
     * @throws URISyntaxException
     * @throws IOException */
    public static String getImagePath(Employee employee, PropertiesUtil propertiesUtil) throws URISyntaxException, IOException {
        String imagePath = propertiesUtil.getProperty("userImagePath") + employee.getIMGURL().substring(employee.getIMGURL().lastIndexOf("/"));
        File file = new File(imagePath);
        if (!file.exists()) {
            imagePath = writeImageToDisk(employee.getIMGURL(), propertiesUtil, imagePath);
        }
        return imagePath;
    }

    /** @return
     * @throws ParseException */
    public static int getMailCounter(int previousMailsDay) throws ParseException {
        Date lastSentDate = getLastSentDate();
        if (lastSentDate == null) {
            return 1;
        }
        DateTime dtLast = new DateTime(lastSentDate.getTime());
        int mailCounter = Days.daysBetween(dtLast.toLocalDate(), new DateTime().toLocalDate()).getDays();

        // If month is different

        if (mailCounter > previousMailsDay) {
            mailCounter = previousMailsDay;
        }
        return mailCounter;
    }

    /** @return
     * @throws ParseException */
    public static Date getLastSentDate() throws ParseException {
        File flagFile = getFlagFile();
        if (flagFile != null) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(flagFile));
                DateFormat formater = new SimpleDateFormat("MM/dd/yyyy");
                Date mailDate = formater.parse(p.getProperty("mail.sent.date"));
                return mailDate;
            } catch (FileNotFoundException e) {
                logger.info("Flag file does not exists.");
            } catch (IOException e) {
                logger.info("Unable to read flag file");
            }
        }
        return null;
    }

    /** @throws FileNotFoundException
     * @throws IOException */
    public static void updateFlagFile(Date mailDate) throws FileNotFoundException, IOException {
        Properties p = new Properties();
        DateFormat formater = new SimpleDateFormat("MM/dd/yyyy");
        p.setProperty("mail.sent.date", formater.format(mailDate));
        p.store(new FileOutputStream(getFlagFile()), "Flag File Update for " + formater.format(mailDate));
    }

    /** @return */
    public static File getFlagFile() {
        String flagFile = null;
        if (SystemUtils.IS_OS_LINUX) {
            flagFile = "/usr/local/bwisher.flag";
        } else if (SystemUtils.IS_OS_WINDOWS) {
            flagFile = "c:\bwisher.flag";
        }
        File file = new File(flagFile);
        return file;
    }

    /** @param mailDate2
     * @return
     * @throws ParseException */
    public static boolean checkMailStatus(Date mailDate) throws ParseException {
        File flagFile = getFlagFile();
        boolean mailSent = false;
        if (flagFile != null) {
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(flagFile));
                DateFormat formater = new SimpleDateFormat("MM/dd/yyyy");
                Date lastMailDate = formater.parse(p.getProperty("mail.sent.date"));
                if (mailDate.getDate() == lastMailDate.getDate()) {
                    mailSent = true;
                }
            } catch (FileNotFoundException e) {
                logger.info("Flag file does not exists.");
            } catch (IOException e) {
                logger.info("Unable to read flag file");
            }
        }
        return mailSent;
    }

    public int getPreviousMailSentDays() {
        return previousMailSentDays;
    }

    public void setPreviousMailSentDays(int previousMailSentDays) {
        this.previousMailSentDays = previousMailSentDays;
    }

    /** @param session
     * @param bdMsgImage
     * @param employee
     * @return
     * @throws MessagingException
     * @throws IOException */
    public static Message buildMessageWithEmbeddedImage(Session session, String bdMsgImage, Employee employee, PropertiesUtil propertiesUtil)
            throws MessagingException, IOException {
        SMTPMessage m = new SMTPMessage(session);
        MimeMultipart content = new MimeMultipart("related");
        String cid = ContentIdGenerator.getContentId();
        MimeBodyPart textPart = new MimeBodyPart();
        textPart.setText(getHtmlContent(employee, cid), "US-ASCII", "html");
        content.addBodyPart(textPart);
        MimeBodyPart imagePart = new MimeBodyPart();
        imagePart.attachFile(propertiesUtil.getProperty("userImagePath") + bdMsgImage);
        imagePart.setContentID("<" + cid + ">");
        imagePart.setDisposition(MimeBodyPart.INLINE);
        content.addBodyPart(imagePart);
        m.setContent(content);
        m.setSubject(employee.getSUBJECT());
        return m;
    }

    /** @param employee
     * @param cid
     * @return */
    public static String getHtmlContent(Employee employee, String cid) {
        String htmlContent = "<html>" + "<header>" + "<title>Subject</title>" + "</header>" + "<body>"
                + " <table width='1200px' height='700px' align='center' border='0' cellspacing='0' cellpadding='0' >" + "<tr>" + "<td>"
                + "<div style='width: 100%;'>" + "<div style='width: 30%;'>" + "<h2>Dear " + employee.getNAME() + " </h2>" + "</div>" + "<div>"
                + "<img src='cid:" + cid + "'/>" + "</div>" + "</div>" + "</td>" + "</tr>" + "<tr>" + "<td>" + "<h3></br>Regards</br>Team Spark</h3>"
                + "</td>" + "</tr>" + "</table>" + "</body>" + "</html>";
        return htmlContent;
    }
}
