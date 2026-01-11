package gr.teipir.studyrooms.dto.api;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO για αιτήματα login.
 * Περιέχει username και password με validation για μη κενές τιμές.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username is required") // Το username δεν μπορεί να είναι κενό
    private String username;

    @NotBlank(message = "Password is required") // Το password δεν μπορεί να είναι κενό
    private String password;
}

/**
 * DTO για επιτυχημένη απόκριση authentication.
 * Περιλαμβάνει token, username, role και μήνυμα.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class AuthResponse {

    private String token;    // Το JWT token
    private String username; // Όνομα χρήστη
    private String role;     // Ρόλος χρήστη
    private String message;  // Μήνυμα επιτυχίας

    // Constructor χωρίς μήνυμα, ορίζουμε default μήνυμα επιτυχίας
    public AuthResponse(String token, String username, String role) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.message = "Authentication successful";
    }
}

/**
 * DTO για σφάλματα API.
 * Περιλαμβάνει μήνυμα λάθους και HTTP status.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ErrorResponse {

    private String error;   // Περιγραφή λάθους
    private String message; // Αναλυτικό μήνυμα
    private int status;     // Κωδικός HTTP
}

/**
 * Γενικό DTO για όλες τις απαντήσεις API.
 * Μπορεί να είναι επιτυχής ή αποτυχία, με δεδομένα οποιουδήποτε τύπου.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
class ApiResponse<T> {

    private boolean success; // true αν επιτυχία, false αν σφάλμα
    private String message;  // Μήνυμα απάντησης
    private T data;          // Δεδομένα απάντησης

    /**
     * Δημιουργούμε επιτυχημένη απάντηση με δεδομένα.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, "Success", data);
    }

    /**
     * Δημιουργούμε επιτυχημένη απάντηση με προσαρμοσμένο μήνυμα και δεδομένα.
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Δημιουργούμε απάντηση σφάλματος με μήνυμα.
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }
}
