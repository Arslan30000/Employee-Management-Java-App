package com.coresync.services;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class AuthService {
    public static String authenticate(String empId, String password) throws Exception {
        String jsonInput = String.format("{\"employee_id\":\"%s\", \"password\":\"%s\"}", empId, password);
        
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ApiConfig.endpoint("login.php")))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonInput))
                .build(); 

        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("API Response Status: " + response.statusCode());
            System.out.println("API Response Body: " + response.body());
            return response.body();
        } catch (Exception e) {
            System.err.println("Authentication Error: " + e.getMessage());
            e.printStackTrace();
            throw new Exception("Failed to connect to authentication server. Make sure XAMPP/PHP backend is running at " + ApiConfig.endpoint("login.php"), e);
        }
    }
}