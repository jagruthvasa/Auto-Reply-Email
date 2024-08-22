package com.emailparser.listener;

import com.emailparser.service.MessageHandlerService;
import jakarta.mail.Message;
import jakarta.mail.event.MessageCountEvent;
import jakarta.mail.event.MessageCountListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EmailMessageListener implements MessageCountListener {

    @Autowired
    private MessageHandlerService messageHandlerService;

    @Override
    public void messagesAdded(MessageCountEvent ev) {
        Message[] messages = ev.getMessages();
        for (Message message : messages) {
            try {
                messageHandlerService.handleMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void messagesRemoved(MessageCountEvent ev) {
    }
}

