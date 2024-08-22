package com.emailparser.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    @Autowired
    private Session mailSession;

    @Autowired
    private Environment env;

    public void sendEmail(String to, String subject, String body) throws MessagingException {
        try {
            Message message = new MimeMessage(mailSession);
            message.setFrom(new InternetAddress(env.getProperty("email.address")));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);

            Transport transport = mailSession.getTransport("smtp");
            transport.connect(env.getProperty("email.address"), env.getProperty("email.password"));
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
