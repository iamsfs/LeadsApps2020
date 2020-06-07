package com.threeaspen.merchant.lite;


import android.util.Log;

import javax.mail.PasswordAuthentication;
import java.security.Security;
import java.util.Properties;


import javax.activation.DataHandler;
import javax.mail.Message;

import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.util.ByteArrayDataSource;


/**
 * Created by AlexanderFomich on 03.05.16.
 */
public class MailHelper extends javax.mail.Authenticator {
    static {
        Security.addProvider(new JSSEProvider());
    }
    private String mailhost = "smtp.gmail.com";
    private String user = "support@cjtechapps.com";
    private String password = "Johnk@420";
    private Session session;
    boolean success = true;

    public MailHelper() {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.host", mailhost);
        props.put("mail.smtp.user", user);
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.debug", "true");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");
         session = Session.getDefaultInstance(props,this);
    }
    protected PasswordAuthentication getPasswordAuthentication() {
        return new PasswordAuthentication(user, password);
    }
    public synchronized void sendMail(
                                       String recipients,String name,String bn,
                                       String email,String pass,String cardProcessing,String source) throws Exception
    {
        try {
            String bodyMassege = "";
            MimeMessage message = new MimeMessage(session);
            final String subject = "New Lead From "+source ;

            bodyMassege =    "Name: <strong>"+name+"</strong> <br><br>" +
                             "Business Name: <strong>"+bn+"</strong><br><br>"+
                             "Email: <strong>"+email+"</strong><br><br>" +
                             "Phone: <strong>"+pass+"</strong><br><br>"+
                             "Processing Credit Cards: <strong>"+cardProcessing+"</strong><br><br>";

            final String body = bodyMassege ;
            //text/plain
            DataHandler handler = new DataHandler(new ByteArrayDataSource(body.getBytes(), "text/html"));
            message.setSender(new InternetAddress("support@cjtechapps.com"));
            message.setSubject(subject);
            message.setDataHandler(handler);
            if (recipients.indexOf(',') > 0)
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipients));
            else
                message.setRecipient(Message.RecipientType.TO, new InternetAddress(recipients));
            Transport.send(message);
        } catch (Exception e) {
            Log.v("SSV", e.toString());

        }
    }
}
