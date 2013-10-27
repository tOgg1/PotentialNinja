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
    private String port = "587";

    private String email_from = "ninjasheep@tormodhaugland.com";

    private static boolean debug = true;

    public SMTPHandler(){


    }

    public boolean sendMail(String email_to, String message_content, String message_subject){
        Properties properties;
        properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);

        Session session = Session.getDefaultInstance(properties, null);
        session.setDebug(this.debug);

        try{
            Message mailMessage = new MimeMessage(session);
            mailMessage.setFrom(new InternetAddress(this.email_from));
            mailMessage.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email_to));
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

    public static void main(String[] args){
        SMTPHandler handler = new SMTPHandler();
        handler.sendMail("tormodh@stud.ntnu.no", "Hei", "Kult");
    }
}
