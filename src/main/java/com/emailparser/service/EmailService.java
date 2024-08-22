package com.emailparser.service;

import jakarta.mail.*;
import jakarta.mail.event.MessageCountListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.core.env.Environment;

@Service
public class EmailService {

    private Store store;
    private Folder inbox;

    @Autowired
    private Environment env;

    @Autowired
    private Session mailSession;

    public void setupMailConnection(MessageCountListener listener) throws MessagingException {
        try {
            store = mailSession.getStore("imaps");
            store.connect("imap.gmail.com", env.getProperty("email.address"), env.getProperty("email.password"));
            inbox = store.getFolder("INBOX");
            inbox.open(Folder.READ_ONLY);
            inbox.addMessageCountListener(listener);

            System.out.println("IMAP connection established successfully.");
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    public Folder getInbox() {
        return inbox;
    }

    public void closeConnections() {
        closeFolder(inbox);
        closeStore(store);
    }

    private void closeFolder(Folder folder) {
        if (folder != null && folder.isOpen()) {
            try {
                folder.close(true);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    private void closeStore(Store store) {
        if (store != null && store.isConnected()) {
            try {
                store.close();
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }
}

