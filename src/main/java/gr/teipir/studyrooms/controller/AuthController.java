package gr.teipir.studyrooms.controller;

import gr.teipir.studyrooms.dto.RegistrationForm;
import gr.teipir.studyrooms.model.User;
import gr.teipir.studyrooms.model.UserRole;
import gr.teipir.studyrooms.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller για το Web UI authentication.
 * Εδώ χειριζόμαστε τη φόρμα εγγραφής χρηστών.
 */
@Controller
public class AuthController {

    // Repository για πρόσβαση στους χρήστες της βάσης
    private final UserRepository userRepository;

    // Encoder για ασφαλή αποθήκευση κωδικών
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor με dependency injection.
     */
    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * GET /register
     * Εμφανίζουμε τη φόρμα εγγραφής.
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {

        // Δημιουργούμε νέο RegistrationForm και ορίζουμε default role
        RegistrationForm form = new RegistrationForm();
        form.setRole(UserRole.STUDENT); // default

        // Προσθέτουμε το form και τις επιλογές roles στο model
        model.addAttribute("form", form);
        model.addAttribute("roles", UserRole.values());

        return "register"; // επιστρέφουμε το όνομα του template
    }

    /**
     * POST /register
     * Επεξεργαζόμαστε την υποβολή της φόρμας εγγραφής.
     */
    @PostMapping("/register")
    public String processRegister(@Valid @ModelAttribute("form") RegistrationForm form,
                                  BindingResult bindingResult,
                                  Model model) {

        /**
         * Ελέγχουμε για validation errors.
         * Αν υπάρχουν, εμφανίζουμε ξανά τη φόρμα με τα μηνύματα σφάλματος.
         */
        if (bindingResult.hasErrors()) {
            model.addAttribute("roles", UserRole.values());
            return "register";
        }

        /**
         * Ελέγχουμε αν υπάρχει ήδη χρήστης με το ίδιο username.
         * Αν ναι, επιστρέφουμε μήνυμα λάθους.
         */
        if (userRepository.findByUsername(form.getUsername()).isPresent()) {
            model.addAttribute("roles", UserRole.values());
            model.addAttribute("errorMessage", "Username already exists");
            return "register";
        }

        /**
         * Δημιουργούμε νέο χρήστη, κωδικοποιούμε τον κωδικό και τον αποθηκεύουμε.
         */
        User user = User.builder()
                .username(form.getUsername())
                .password(passwordEncoder.encode(form.getPassword()))
                .fullName(form.getFullName())
                .email(form.getEmail())
                .role(form.getRole())
                .build();

        userRepository.save(user);

        /**
         * Μετά την επιτυχή εγγραφή, κάνουμε redirect στη σελίδα login
         * με παράμετρο που υποδεικνύει ότι η εγγραφή ολοκληρώθηκε.
         */
        return "redirect:/login?registered";
    }
}
