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

            // printing the content
            StringBuilder messageBuilder = new StringBuilder();

            messageBuilder.append("SUBJECT: ").append(message.getSubject()).append("\n\n");

            Address[] fromAddresses = message.getFrom();
            if (fromAddresses != null && fromAddresses.length > 0) {
                messageBuilder.append("FROM: ").append(((InternetAddress) fromAddresses[0]).getAddress()).append("\n\n");
            }

            Address[] toAddresses = message.getRecipients(Message.RecipientType.TO);
            if (toAddresses != null && toAddresses.length > 0) {
                messageBuilder.append("TO: ");
                for (Address address : toAddresses) {
                    messageBuilder.append(((InternetAddress) address).getAddress()).append(", ");
                }
                messageBuilder.setLength(messageBuilder.length() - 2); // Remove last comma and space
                messageBuilder.append("\n\n");
            }

            Address[] ccAddresses = message.getRecipients(Message.RecipientType.CC);
            if (ccAddresses != null && ccAddresses.length > 0) {
                messageBuilder.append("CC: ");
                for (Address address : ccAddresses) {
                    messageBuilder.append(((InternetAddress) address).getAddress()).append(", ");
                }
                messageBuilder.setLength(messageBuilder.length() - 2); // Remove last comma and space
                messageBuilder.append("\n\n");
            }

            StringBuilder bodyText = new StringBuilder();
            collectTextFromMessage(bodyText, message);
            messageBuilder.append("BODY: ").append(bodyText.toString()).append("\n\n");

            if (message.isMimeType("multipart/*")) {
                Multipart multipart = (Multipart) message.getContent();
                for (int i = 0; i < multipart.getCount(); i++) {
                    Part part = multipart.getBodyPart(i);
                    if (Part.ATTACHMENT.equalsIgnoreCase(part.getDisposition())) {
                        messageBuilder.append("ATTACHMENT: ").append(part.getFileName()).append("\n\n");
                    }
                }
            }

            System.out.println(messageBuilder.toString());

            // Reply for the mail
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

