package gr.teipir.studyrooms.service;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

/**
 * Service για αποστολή ειδοποιήσεων.
 * Παράδειγμα: στέλνει επιβεβαίωση κράτησης σε εξωτερικό API (π.χ. webhook.site για testing).
 */
@Service
public class NotificationService {

    private final RestTemplate restTemplate = new RestTemplate();

    // URL εξωτερικού endpoint (mock API για testing)
    private static final String EXTERNAL_API_URL = "https://webhook.site/9e6421ac-5708-4f67-a077-fa207a088dca";

    /**
     * Στέλνει επιβεβαίωση κράτησης σε email χρήστη.
     * @param email Το email του χρήστη
     * @param roomName Το όνομα της αίθουσας που κρατήθηκε
     */
    public void sendBookingConfirmation(String email, String roomName) {
        try {
            // 1. Δημιουργία JSON σώματος
            Map<String, String> requestBody = new HashMap<>();
            requestBody.put("email", email);
            requestBody.put("message", "Your booking for " + roomName + " is confirmed!");

            // 2. Δημιουργία Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer my-secret-external-api-key-123"); // demo key

            HttpEntity<Map<String, String>> request = new HttpEntity<>(requestBody, headers);

            // 3. Αποστολή POST
            restTemplate.postForEntity(EXTERNAL_API_URL, request, String.class);

            System.out.println("✅ Notification sent via POST to external service.");

        } catch (Exception e) {
            // Fail-safe για να μην αποτύχει η εφαρμογή αν η ειδοποίηση δεν σταλεί
            System.err.println("⚠️ Failed to send notification: " + e.getMessage());
        }
    }
}
