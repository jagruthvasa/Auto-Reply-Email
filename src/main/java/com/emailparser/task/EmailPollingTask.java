package com.emailparser.task;


import com.emailparser.listener.EmailMessageListener;
import com.emailparser.service.EmailService;
import jakarta.mail.Folder;
import jakarta.mail.MessagingException;
import jakarta.mail.StoreClosedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class EmailPollingTask {

    @Autowired
    private EmailService emailService;

    @Autowired
    private EmailMessageListener emailMessageListener;

    @Scheduled(fixedRate = 30000) // every 30 seconds
    public void pollEmailInbox() {
        Folder inbox = emailService.getInbox();
        try {
            if (inbox != null && inbox.isOpen()) {
                inbox.getMessageCount();
            } else {
                emailService.setupMailConnection(emailMessageListener);
            }
        } catch (MessagingException e) {
            if (e.getCause() instanceof StoreClosedException) {
                emailService.closeConnections();
                try {
                    emailService.setupMailConnection(emailMessageListener);
                } catch (MessagingException ex) {
                    ex.printStackTrace();
                }
            } else {
                e.printStackTrace();
            }
        }
    }
}

