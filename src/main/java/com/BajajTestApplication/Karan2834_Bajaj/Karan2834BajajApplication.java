package com.BajajTestApplication.Karan2834_Bajaj;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@SpringBootApplication
public class Karan2834BajajApplication {

	public static void main(String[] args) {
		SpringApplication.run(Karan2834BajajApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

	@Bean
	public CommandLineRunner run(RestTemplate restTemplate) {
		return args -> {
			try {
				// User Configuration
				String myName = "Karan Gautam";
				String myRegNo = "22BCE2834";
				String myEmail = "karan.gautam2022@vitstudent.ac.in";
				String generateUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

				System.out.println("Starting execution for: " + myName);

				// 1. Generate Webhook and Get Token
				Map<String, String> requestBody = new HashMap<>();
				requestBody.put("name", myName);
				requestBody.put("regNo", myRegNo);
				requestBody.put("email", myEmail);

				HttpHeaders headers = new HttpHeaders();
				headers.setContentType(MediaType.APPLICATION_JSON);

				HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

				ResponseEntity<Map> response = restTemplate.postForEntity(generateUrl, request, Map.class);
				Map<String, Object> body = response.getBody();

				String accessToken = (String) body.get("accessToken");
				String webhookUrl = (String) body.get("webhookUrl");

				System.out.println("Token Received. Webhook URL: " + webhookUrl);

				// 2. Prepare SQL Solution (Question 2 - Even)
				String finalSqlQuery = "SELECT d.DEPARTMENT_NAME, " +
						"AVG(TIMESTAMPDIFF(YEAR, unique_emp.DOB, CURDATE())) AS AVERAGE_AGE, " +
						"SUBSTRING_INDEX(GROUP_CONCAT(CONCAT(unique_emp.FIRST_NAME, ' ', unique_emp.LAST_NAME) SEPARATOR ', '), ', ', 10) AS EMPLOYEE_LIST " +
						"FROM DEPARTMENT d " +
						"JOIN (SELECT DISTINCT e.EMP_ID, e.FIRST_NAME, e.LAST_NAME, e.DOB, e.DEPARTMENT " +
						"FROM EMPLOYEE e JOIN PAYMENTS p ON e.EMP_ID = p.EMP_ID WHERE p.AMOUNT > 70000) unique_emp " +
						"ON d.DEPARTMENT_ID = unique_emp.DEPARTMENT " +
						"GROUP BY d.DEPARTMENT_ID, d.DEPARTMENT_NAME " +
						"ORDER BY d.DEPARTMENT_ID DESC";

				// 3. Submit Solution
				HttpHeaders authHeaders = new HttpHeaders();
				authHeaders.setContentType(MediaType.APPLICATION_JSON);
				authHeaders.set("Authorization", accessToken);

				Map<String, String> finalBody = new HashMap<>();
				finalBody.put("finalQuery", finalSqlQuery);

				HttpEntity<Map<String, String>> finalRequest = new HttpEntity<>(finalBody, authHeaders);

				// Use the received webhook URL, fallback to default if null
				String targetUrl = (webhookUrl != null) ? webhookUrl : "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";

				ResponseEntity<String> submitResponse = restTemplate.postForEntity(targetUrl, finalRequest, String.class);

				System.out.println("Final Response Code: " + submitResponse.getStatusCode());
				System.out.println("Final Response Body: " + submitResponse.getBody());

			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}
}