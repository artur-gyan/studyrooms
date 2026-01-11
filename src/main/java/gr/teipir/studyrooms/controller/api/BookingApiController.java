package gr.teipir.studyrooms.controller.api;

import gr.teipir.studyrooms.dto.BookingForm;
import gr.teipir.studyrooms.model.Booking;
import gr.teipir.studyrooms.model.User;
import gr.teipir.studyrooms.model.UserRole;
import gr.teipir.studyrooms.repository.UserRepository;
import gr.teipir.studyrooms.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller για τη διαχείριση κρατήσεων μέσω API.
 * Εδώ χειριζόμαστε την προβολή, δημιουργία και ακύρωση κρατήσεων.
 */
@RestController
@RequestMapping("/api/bookings")
public class BookingApiController {

    // Service που περιέχει τη βασική επιχειρησιακή λογική των κρατήσεων
    private final BookingService bookingService;

    // Repository για ανάκτηση στοιχείων χρηστών από τη βάση
    private final UserRepository userRepository;

    /**
     * Constructor με dependency injection.
     */
    public BookingApiController(BookingService bookingService, UserRepository userRepository) {
        this.bookingService = bookingService;
        this.userRepository = userRepository;
    }

    /**
     * GET /api/bookings
     * Επιστρέφουμε όλες τις κρατήσεις.
     * - Το προσωπικό βιβλιοθήκης βλέπει όλες τις κρατήσεις
     * - Οι φοιτητές βλέπουν μόνο τις δικές τους
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getAllBookings() {

        /**
         * Ανακτούμε το username του authenticated χρήστη
         * από το SecurityContext.
         */
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        /**
         * Φορτώνουμε τον χρήστη από τη βάση.
         */
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        List<Booking> bookings;

        /**
         * Αν ο χρήστης είναι LIB_STAFF,
         * επιστρέφουμε όλες τις κρατήσεις.
         * Διαφορετικά, επιστρέφουμε μόνο τις δικές του.
         */
        if (user.getRole() == UserRole.LIB_STAFF) {
            bookings = bookingService.getAllBookings();
        } else {
            bookings = bookingService.getBookingsForUser(user);
        }

        /**
         * Δημιουργούμε το response.
         */
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", bookings);
        response.put("count", bookings.size());

        return ResponseEntity.ok(response);
    }

    /**
     * POST /api/bookings
     * Δημιουργούμε νέα κράτηση.
     * Επιτρέπεται μόνο σε χρήστες με ρόλο STUDENT.
     */
    @PostMapping
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Map<String, Object>> createBooking(
            @Valid @RequestBody BookingForm form) {
        try {

            /**
             * Καλούμε το service για τη δημιουργία της κράτησης.
             * Όλη η επιχειρησιακή λογική ελέγχων βρίσκεται εκεί.
             */
            bookingService.createBooking(form);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking created successfully");

            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(response);

        } catch (IllegalArgumentException | IllegalStateException e) {

            /**
             * Σε περίπτωση σφάλματος (π.χ. μη διαθέσιμη αίθουσα),
             * επιστρέφουμε HTTP 400 με μήνυμα λάθους.
             */
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error);
        }
    }

    /**
     * POST /api/bookings/{id}/cancel
     * Ακυρώνουμε υπάρχουσα κράτηση.
     */
    @PostMapping("/{id}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> cancelBooking(@PathVariable Long id) {
        try {

            /**
             * Καλούμε το service για ακύρωση της κράτησης
             * με βάση το id.
             */
            bookingService.cancelBooking(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Booking cancelled successfully");

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException | IllegalStateException e) {

            /**
             * Αν προκύψει σφάλμα (π.χ. μη έγκυρο id),
             * επιστρέφουμε HTTP 400.
             */
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(error);
        }
    }

    /**
     * GET /api/bookings/my
     * Επιστρέφουμε τις κρατήσεις του τρέχοντος χρήστη.
     */
    @GetMapping("/my")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getMyBookings() {

        /**
         * Ανακτούμε το username του authenticated χρήστη.
         */
        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        /**
         * Φορτώνουμε τον χρήστη από τη βάση.
         */
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("User not found"));

        /**
         * Παίρνουμε όλες τις κρατήσεις του συγκεκριμένου χρήστη.
         */
        List<Booking> bookings = bookingService.getBookingsForUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", bookings);
        response.put("count", bookings.size());

        return ResponseEntity.ok(response);
    }
}
