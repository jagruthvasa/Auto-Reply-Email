package com.emailparser.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.io.IOException;

@Service
public class GeminiService {

    @Autowired
    private Environment env;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public String getGeminiResponse(String subject, String body) {
        String apiKey = env.getProperty("gemini.api.key");
        String apiUrl = env.getProperty("gemini.api.url");

        String prompt = "You are an automated email responder for an organization named Test. Your task is to generate a professional and courteous acknowledgment response for the received email. \n" +
                "Here is the email content:\n" +
                "Subject: " + subject + "\n" +
                "Body:\n" + body + "\n" +
                "---\n" +
                "Please create an acknowledgment response with the following requirements:\n" +
                "1. Acknowledge receipt of the email.\n" +
                "2. Provide a generic but polite response indicating that a detailed reply will follow as soon as possible.\n" +
                "3. Sign off with the organization name 'Test'.\n" +
                "The response should be concise, professional, and respectful.\n" +
                "Note: When generating the response, include only the body of the response in your output. Do not include the subject of the email in the response.";

        String requestBody = "{\"contents\":[{\"parts\":[{\"text\":\"" + prompt + "\"}]}]}";

        try {
            String response = restTemplate.postForObject(apiUrl + "?key=" + apiKey, requestBody, String.class);
            return parseResponseText(response);
        } catch (RestClientException e) {
            e.printStackTrace();
            return "Issue on connecting with gemini api";
        }
    }

    private String parseResponseText(String response) {
        try {
            JsonNode rootNode = objectMapper.readTree(response);
            JsonNode candidatesNode = rootNode.path("candidates").get(0);
            JsonNode contentNode = candidatesNode.path("content");
            JsonNode partsNode = contentNode.path("parts").get(0);
            return partsNode.path("text").asText();
        } catch (IOException e) {
            e.printStackTrace();
            return "Error parsing Gemini API response.";
        }
    }
}