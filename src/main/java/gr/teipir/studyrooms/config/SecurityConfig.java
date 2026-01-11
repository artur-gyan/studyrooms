package gr.teipir.studyrooms.config;

import gr.teipir.studyrooms.security.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Κεντρική ρύθμιση ασφάλειας της εφαρμογής.
 * Εδώ ορίζουμε:
 * - κανόνες πρόσβασης (authorization)
 * - μηχανισμό authentication (JWT & form login)
 * - session policy
 * - φίλτρα ασφαλείας
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    // Custom φίλτρο για έλεγχο και επαλήθευση JWT token
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Constructor με dependency injection του JWT filter.
     */
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    /**
     * Ορίζουμε PasswordEncoder.
     * Χρησιμοποιούμε BCrypt για ασφαλή αποθήκευση κωδικών.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Ορίζουμε AuthenticationManager.
     * Το Spring τον χρησιμοποιεί για τη διαδικασία authentication.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Κύρια ρύθμιση του Security Filter Chain.
     * Εδώ καθορίζουμε:
     * - CSRF
     * - δικαιώματα πρόσβασης
     * - session policy
     * - login behavior
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                /**
                 * Ρύθμιση CSRF.
                 * Απενεργοποιούμε το CSRF για API endpoints,
                 * επειδή χρησιμοποιούμε JWT (stateless authentication).
                 */
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/api/**", "/h2-console/**")
                )

                /**
                 * Ρύθμιση κανόνων πρόσβασης (authorization).
                 * Καθορίζουμε ποια endpoints είναι public
                 * και ποια απαιτούν authentication ή συγκεκριμένο ρόλο.
                 */
                .authorizeHttpRequests(auth -> auth

                        // Δημόσια πρόσβαση (χωρίς authentication)
                        .requestMatchers("/", "/h2-console/**", "/register", "/css/**", "/js/**").permitAll()

                        // API endpoints για authentication (login / register)
                        .requestMatchers("/api/auth/**").permitAll()

                        // API Rooms:
                        // GET: δημόσια πρόσβαση
                        // POST / PUT: μόνο για ρόλο LIB_STAFF
                        .requestMatchers(HttpMethod.GET, "/api/rooms", "/api/rooms/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/rooms").hasRole("LIB_STAFF")
                        .requestMatchers(HttpMethod.PUT, "/api/rooms/**").hasRole("LIB_STAFF")

                        // API Bookings: απαιτείται authentication
                        .requestMatchers("/api/bookings/**").authenticated()

                        // Web UI Rooms:
                        // Προβολή για όλους, δημιουργία μόνο από LIB_STAFF
                        .requestMatchers(HttpMethod.GET, "/rooms").permitAll()
                        .requestMatchers(HttpMethod.GET, "/rooms/new").hasRole("LIB_STAFF")
                        .requestMatchers(HttpMethod.POST, "/rooms").hasRole("LIB_STAFF")

                        // Web UI Bookings: μόνο authenticated χρήστες
                        .requestMatchers("/bookings/**").authenticated()

                        // Οποιοδήποτε άλλο request απαιτεί authentication
                        .anyRequest().authenticated()
                )

                /**
                 * Ρύθμιση session management.
                 * Χρησιμοποιούμε IF_REQUIRED ώστε:
                 * - το API να λειτουργεί stateless (JWT)
                 * - το web UI να μπορεί να χρησιμοποιεί session αν χρειαστεί
                 */
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                /**
                 * Ρύθμιση form login για το web UI.
                 * Μετά από επιτυχημένο login,
                 * γίνεται πάντα redirect στη σελίδα "/".
                 */
                .formLogin(login -> login
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                );

        /**
         * Προσθέτουμε το JWT φίλτρο πριν από το
         * UsernamePasswordAuthenticationFilter.
         * Έτσι ελέγχουμε πρώτα το JWT token σε κάθε request.
         */
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        // Δημιουργούμε και επιστρέφουμε το SecurityFilterChain
        return http.build();
    }
}
