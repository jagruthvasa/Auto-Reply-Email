package com.emailparser.service;

import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class MessageHandlerService {

    @Autowired
    private EmailSenderService emailSenderService;

    @Autowired
    private GeminiService geminiService;

    public void handleMessage(Message message) throws MessagingException, IOException {
        try {
            String subject = message.getSubject();
            String body = getMessageBody(message);
            String sender = ((InternetAddress) message.getFrom()[0]).getAddress();

            // Get Gemini response
            String geminiResponse = geminiService.getGeminiResponse(subject, body);

            // Send acknowledgement with Gemini response
            sendAcknowledgementWithResponse(sender, subject, geminiResponse);
        } catch (MessagingException | IOException e) {
            e.printStackTrace();
        }
    }

    private void sendAcknowledgementWithResponse(String to, String originalSubject, String geminiResponse) throws MessagingException {
        String subject = "Reply: " + originalSubject;

        try {
            emailSenderService.sendEmail(to, subject, geminiResponse);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }


    private String getMessageBody(Message message) throws MessagingException, IOException {
        StringBuilder bodyText = new StringBuilder();
        collectTextFromMessage(bodyText, message);
        return bodyText.toString();
    }

    public void collectTextFromMessage(StringBuilder textCollector, Part part) throws MessagingException, IOException {
        if (part.isMimeType("text/plain")) {
            textCollector.append((String) part.getContent());
        } else if (part.isMimeType("multipart/*") && part.getContent() instanceof Multipart) {
            Multipart multiPart = (Multipart) part.getContent();
            for (int i = 0; i < multiPart.getCount(); i++) {
                collectTextFromMessage(textCollector, multiPart.getBodyPart(i));
            }
        }
    }
}

