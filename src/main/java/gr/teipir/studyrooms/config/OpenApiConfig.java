package gr.teipir.studyrooms.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import org.springframework.context.annotation.Configuration;

/**
 * Ρύθμιση OpenAPI / Swagger.
 * Τη χρησιμοποιούμε για την τεκμηρίωση του REST API
 * και για την ενσωμάτωση μηχανισμού ασφάλειας με JWT.
 */
@Configuration
@OpenAPIDefinition(
        /**
         * Βασικές πληροφορίες για το API που εμφανίζονται στο Swagger UI.
        */
        info = @Info(
                title = "StudyRooms API",
                version = "1.0",
                description = "API for Room Booking System"
        ),

        /**
         * Δηλώνουμε ότι το API απαιτεί authentication
         * με το security scheme που ονομάζεται bearerAuth.
         * Η ρύθμιση αυτή εφαρμόζεται σε όλα τα endpoints.
        */
        security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
        /**
         * Όνομα του security scheme.
         * Το χρησιμοποιούμε στο @SecurityRequirement.
        */
        name = "bearerAuth",

        /**
         * Τύπος ασφάλειας: HTTP authentication.
        */
        type = SecuritySchemeType.HTTP,

        /**
         * Χρησιμοποιούμε το bearer scheme.
        */
        scheme = "bearer",

        /**
         * Δηλώνουμε ότι το bearer token είναι JWT.
         * Αυτό εμφανίζεται και στο Swagger UI.
        */
        bearerFormat = "JWT"
)
public class OpenApiConfig {
    // Δεν απαιτείται επιπλέον κώδικας.
    // Η κλάση χρησιμοποιείται μόνο για configuration μέσω annotations.
}
