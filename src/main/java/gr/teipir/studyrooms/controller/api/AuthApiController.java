package gr.teipir.studyrooms.controller.api;

import gr.teipir.studyrooms.dto.RegistrationForm;
import gr.teipir.studyrooms.dto.api.LoginRequest;
import gr.teipir.studyrooms.model.User;
import gr.teipir.studyrooms.repository.UserRepository;
import gr.teipir.studyrooms.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller για authentication και registration μέσω API.
 * Εδώ υλοποιούμε login και εγγραφή χρηστών με χρήση JWT.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthApiController {

    // Χρησιμοποιούμε τον AuthenticationManager για έλεγχο διαπιστευτηρίων
    private final AuthenticationManager authenticationManager;

    // Φορτώνουμε στοιχεία χρήστη (username, ρόλους κ.λπ.)
    private final UserDetailsService userDetailsService;

    // Utility κλάση για δημιουργία και διαχείριση JWT tokens
    private final JwtUtil jwtUtil;

    // Repository για πρόσβαση στα δεδομένα χρηστών
    private final UserRepository userRepository;

    // Encoder για ασφαλή αποθήκευση κωδικών
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor με dependency injection.
     */
    public AuthApiController(AuthenticationManager authenticationManager,
                             UserDetailsService userDetailsService,
                             JwtUtil jwtUtil,
                             UserRepository userRepository,
                             PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * POST /api/auth/login
     * Πραγματοποιούμε authentication χρήστη
     * και επιστρέφουμε JWT token σε περίπτωση επιτυχίας.
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            /**
             * Προσπαθούμε να κάνουμε authenticate τον χρήστη
             * με βάση το username και το password που έστειλε.
             */
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            /**
             * Αν το authentication πετύχει,
             * φορτώνουμε τα πλήρη στοιχεία του χρήστη.
             */
            final UserDetails userDetails =
                    userDetailsService.loadUserByUsername(loginRequest.getUsername());

            /**
             * Δημιουργούμε JWT token με βάση τα στοιχεία του χρήστη.
             */
            final String token = jwtUtil.generateToken(userDetails);

            /**
             * Ανακτούμε τον ρόλο του χρήστη
             * (π.χ. ROLE_STUDENT, ROLE_LIB_STAFF).
             */
            String role = userDetails.getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority();

            /**
             * Δημιουργούμε το response που θα σταλεί στον client.
             */
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("username", userDetails.getUsername());
            response.put("role", role);
            response.put("message", "Authentication successful");

            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            /**
             * Αν τα διαπιστευτήρια είναι λάθος,
             * επιστρέφουμε HTTP 401 (Unauthorized).
             */
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            error.put("message", "Username or password is incorrect");

            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(error);
        }
    }

    /**
     * POST /api/auth/register
     * Δημιουργούμε νέο χρήστη και επιστρέφουμε JWT token.
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationForm form) {

        /**
         * Ελέγχουμε αν υπάρχει ήδη χρήστης
         * με το ίδιο username.
         */
        if (userRepository.findByUsername(form.getUsername()).isPresent()) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Username already exists");
            error.put("message", "Please choose a different username");

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error);
        }

        /**
         * Δημιουργούμε νέο χρήστη
         * και κωδικοποιούμε τον κωδικό πρόσβασης.
         */
        User user = User.builder()
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .fullName(form.getFullName())
                .email(form.getEmail())
                .role(form.getRole())
                .build();

        // Αποθηκεύουμε τον χρήστη στη βάση
        userRepository.save(user);

        /**
         * Φορτώνουμε τα στοιχεία του χρήστη
         * ώστε να δημιουργήσουμε JWT token.
         */
        final UserDetails userDetails =
                userDetailsService.loadUserByUsername(form.getUsername());

        final String token = jwtUtil.generateToken(userDetails);

        // Ανακτούμε τον ρόλο του χρήστη
        String role = userDetails.getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        /**
         * Δημιουργούμε το response επιτυχούς εγγραφής.
         */
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("username", user.getUsername());
        response.put("role", role);
        response.put("message", "Registration successful");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }
}
