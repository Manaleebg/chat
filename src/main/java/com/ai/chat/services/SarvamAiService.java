package com.ai.chat.services;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ai.chat.models.ChatMessage;


@Service

public class SarvamAiService {
@Value("${sarvam.api.key}")

private String apikey;

@Value("${sarvam.model}")
private String model;

private final RestTemplate restTemplate = new RestTemplate();

public String askSarvam(List<ChatMessage> history, String userMessage) {
	String url = "https://api.sarvam.ai/v1/chat/completions";
	List<Map<String,String>> message = new ArrayList<>();
	
	message.add(Map.of(
			"role","system","content","You are a help AI assistance."));
	
	for (ChatMessage msg: history ) {
		message.add(Map.of(
				"role",msg.getRole(),
				"content",msg.getContent()));
		
				
	}
	message.add(Map.of(
			"role","user",
			"content", userMessage));
	
	Map<String, Object> body = new HashMap<>();
	
	body.put("model",model);
	body.put("messages", message);
	body.put("temperature", 0.2);
	body.put("max_tokens", 1000);
	
 HttpHeaders header = new HttpHeaders();
 header.setContentType(MediaType.APPLICATION_JSON);
 
 header.setBearerAuth(apikey);
 HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body,header);
 
 try {

	    ResponseEntity<Map> response =
	            restTemplate.postForEntity(url, entity, Map.class);

	    System.out.println("SARVAM RESPONSE: " + response.getBody());

	    List choice = (List) response.getBody().get("choices");

	    if (choice == null || choice.isEmpty()) {
	        return "No response from AI";
	    }

	    Map firstChoice = (Map) choice.get(0);
	    Map message1 = (Map) firstChoice.get("message");

	    return message1.get("content").toString();

	} catch (Exception e) {

	    e.printStackTrace();

	    return "Error calling Sarvam AI: " + e.getMessage();
	}
}
}
