package gr.teipir.studyrooms.security;

import gr.teipir.studyrooms.model.User;
import gr.teipir.studyrooms.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Service που φορτώνει τα στοιχεία ενός χρήστη για το Spring Security.
 * Υλοποιεί το UserDetailsService για να χρησιμοποιηθεί στην authentication διαδικασία.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // Repository για αναζήτηση χρηστών

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Φορτώνουμε έναν χρήστη με βάση το username.
     * Αν δεν υπάρχει, πετάμε UsernameNotFoundException.
     *
     * @param username Το username του χρήστη
     * @return UserDetails που χρησιμοποιείται από Spring Security
     * @throws UsernameNotFoundException αν δεν βρεθεί χρήστης
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return new CustomUserDetails(user);
    }
}


