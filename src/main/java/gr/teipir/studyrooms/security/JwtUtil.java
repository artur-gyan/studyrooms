package gr.teipir.studyrooms.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Utility για δημιουργία, εξαγωγή και έλεγχο JWT tokens.
 * Χρησιμοποιούμε HS256 αλγόριθμο για υπογραφή.
 */
@Component
public class JwtUtil {

    // Secret key για υπογραφή JWT (πρέπει να είναι αρκετά μεγάλο)
    private final SecretKey secretKey = Keys.hmacShaKeyFor(
            "MySecretKeyForJWTTokenGenerationThatIsLongEnoughForHS256Algorithm".getBytes()
    );

    // Διάρκεια ζωής token: 24 ώρες (σε milliseconds)
    private static final long JWT_TOKEN_VALIDITY = 24 * 60 * 60 * 1000;

    /**
     * Εξάγουμε το username (subject) από το token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Εξάγουμε την ημερομηνία λήξης από το token
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Εξάγουμε μια συγκεκριμένη claim από το token χρησιμοποιώντας Function
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Εξάγουμε όλες τις claims από το token
     */
    private Claims extractAllClaims(String token) {
        // Κάνουμε parse και επαλήθευση με το secretKey
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Ελέγχουμε αν το token έχει λήξει
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Δημιουργούμε νέο token για τον χρήστη
     */
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // Προσθέτουμε τον ρόλο στα claims
        String role = userDetails.getAuthorities().iterator().next().getAuthority();
        claims.put("role", role);

        return createToken(claims, userDetails.getUsername());
    }

    /**
     * Δημιουργούμε JWT token με claims και subject
     */
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDITY))
                .signWith(secretKey)
                .compact();
    }

    /**
     * Ελέγχουμε αν το token είναι έγκυρο για τον συγκεκριμένο χρήστη
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            // Αν υπάρχει σφάλμα στο parsing, θεωρούμε το token άκυρο
            return false;
        }
    }
}
