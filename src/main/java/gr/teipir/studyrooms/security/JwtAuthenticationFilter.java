package gr.teipir.studyrooms.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Φιλτράρει κάθε HTTP αίτημα για JWT authentication.
 * Ελέγχουμε το Authorization header και ρυθμίζουμε τον χρήστη στο SecurityContext αν το token είναι έγκυρο.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil; // Utility για δημιουργία και έλεγχο JWT
    private final CustomUserDetailsService userDetailsService; // Φορτώνουμε τα στοιχεία χρήστη

    public JwtAuthenticationFilter(JwtUtil jwtUtil, CustomUserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Εκτελείται για κάθε αίτημα HTTP.
     * Ελέγχουμε αν υπάρχει JWT στο header και αν είναι έγκυρο.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Παίρνουμε το Authorization header
        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        // Ελέγχουμε αν το header ξεκινά με "Bearer "
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7); // Αφαιρούμε το "Bearer "
            try {
                username = jwtUtil.extractUsername(jwt); // Παίρνουμε το username από το token
            } catch (Exception e) {
                // Αν το token είναι άκυρο, καταγράφουμε προειδοποίηση
                logger.warn("JWT Token extraction failed: " + e.getMessage());
            }
        }

        // Ελέγχουμε το token και δημιουργούμε Authentication αν δεν υπάρχει ήδη
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Δημιουργούμε authentication token
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken); // Θέτουμε authentication στο context
            }
        }

        // Συνεχίζουμε την αλυσίδα φίλτρων
        filterChain.doFilter(request, response);
    }
}

