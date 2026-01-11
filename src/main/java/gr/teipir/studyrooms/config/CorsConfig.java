package gr.teipir.studyrooms.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.Arrays;

/**
 * Ρύθμιση CORS (Cross-Origin Resource Sharing).
 * Εδώ ορίζουμε τι επιτρέπουμε και τι όχι σε αιτήματα
 * που προέρχονται από διαφορετικό origin (domain / port).
 */
@Configuration
public class CorsConfig {

    /**
     * Δημιουργούμε και καταχωρούμε ένα CorsFilter στο Spring context.
     * Το φίλτρο αυτό εφαρμόζεται σε όλα τα endpoints της εφαρμογής.
     */
    @Bean
    public CorsFilter corsFilter() {

        // Δημιουργούμε το αντικείμενο ρυθμίσεων CORS
        CorsConfiguration config = new CorsConfiguration();

        /**
         * Επιτρέπουμε credentials (cookies, Authorization headers κ.λπ.).
         * Αν δεν το ενεργοποιήσουμε, ο browser μπλοκάρει τέτοια δεδομένα.
         */
        config.setAllowCredentials(true);

        /**
         * Επιτρέπουμε αιτήματα από οποιοδήποτε origin.
         * Το χρησιμοποιούμε ΜΟΝΟ για development.
         * Σε production ορίζουμε συγκεκριμένο domain.
         */
        config.addAllowedOriginPattern("*");

        /**
         * Επιτρέπουμε όλα τα headers στο request
         * (π.χ. Content-Type, Authorization).
         */
        config.addAllowedHeader("*");

        /**
         * Επιτρέπουμε όλες τις HTTP μεθόδους
         * (GET, POST, PUT, DELETE, PATCH, OPTIONS).
         */
        config.addAllowedMethod("*");

        /**
         * Δηλώνουμε ποια headers επιτρέπουμε να είναι ορατά στον client.
         * Χωρίς αυτό, το Authorization header δεν είναι προσβάσιμο
         * από JavaScript στο frontend.
         */
        config.setExposedHeaders(Arrays.asList("Authorization"));

        /**
         * Δημιουργούμε το source που αντιστοιχίζει
         * τις CORS ρυθμίσεις σε URL patterns.
         */
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        /**
         * Εφαρμόζουμε τις CORS ρυθμίσεις σε όλα τα endpoints (/**).
         */
        source.registerCorsConfiguration("/**", config);

        /**
         * Επιστρέφουμε το φίλτρο CORS που εκτελείται
         * πριν φτάσει το request στους controllers.
         */
        return new CorsFilter(source);
    }
}