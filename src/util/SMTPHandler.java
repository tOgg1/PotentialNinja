package util;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.*;
import java.util.Date;
import java.util.Properties;

public class SMTPHandler {
    private String host = "smtp.gmail.com";
    private String port = "587";

    private String email_from = "ninjasheep@tormodhaugland.com";
    private String email_account;
    private String email_password;
    private int email_password_factory;

    private static String filename = "config.txt";

    private static boolean debug = false;
    private static boolean readyToGo;

    public SMTPHandler(){
        File file = new File(this.filename);
        this.readyToGo = false;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String account = reader.readLine();

            if(account == null){
                throw new RuntimeException("Error reading ´config.txt´: Missing account");
            }
            else{
                this.email_account = account;
            }

            String password = reader.readLine();

            if(password == null){
                throw new RuntimeException("Error reading ´config.txt´: Missing password");
            }else{
                this.email_password = password;
            }

            String factoryCode = reader.readLine();

            if(factoryCode == null){
                throw new RuntimeException("Error reading ´config.txt´: Missing factory code");
            }
            else{
                try{
                    this.email_password_factory = Integer.parseInt(factoryCode);
                }catch(NumberFormatException e){
                    throw new RuntimeException("Error reading ´config.txt´: Invalid factory code");
                }
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("File ´config.txt´ not found, or is corrupted");
        } catch (IOException e) {
            throw new RuntimeException("Error reading ´config.txt´");
        }
        this.readyToGo = true;
    }

    public boolean sendMail(String email_to, String message_content, String message_subject){
        if(!readyToGo)
            return false;
        Properties properties;
        properties = System.getProperties();
        properties.put("mail.smtp.host", host);
        properties.put("mail.smtp.port", port);
        properties.put("mail.smtp.starttls.enable", "true");
        properties.put("mail.smtp.auth", "true");

        Session session = Session.getInstance(properties, new Authenticator(){
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(email_account, NothingToSeeHere.f(email_password, email_password_factory));
            }
        });
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
}
