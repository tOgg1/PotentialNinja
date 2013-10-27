import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Date;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: Mayacat
 * Date: 10/24/13
 * Time: 9:44 AM
 * To change this template use File | Settings | File Templates.
 */
public class SMTPHandler {
    private String host = "smtp.domeneshop.no";
    private String email_from = "ninjasheep@tormodhaugland.com";

    Properties properties;

    private static boolean debug = true;

    public SMTPHandler(){

        boolean sessionDebug = false;
        properties = System.getProperties();
        properties.put("mail.host", host);
        properties.put("mail.transport.protocol", "smtp");
    }

    public boolean sendMail(String email_to, String message_content, String message_subject){
        Session session = Session.getDefaultInstance(this.properties, null);
        session.setDebug(this.debug);

        try{
            Message mailMessage = new MimeMessage(session);
            mailMessage.setFrom(new InternetAddress(this.email_from));
            mailMessage.setRecipients(Message.RecipientType.TO, new InternetAddress[]{new InternetAddress(email_to)});
            mailMessage.setSubject(message_subject);
            mailMessage.setSentDate(new Date());
            mailMessage.setText(message_content);
            Transport.send(mailMessage);
            return true;
        } catch(MessagingException e){
            e.printStackTrace();
            return false;
        }
    }

}
