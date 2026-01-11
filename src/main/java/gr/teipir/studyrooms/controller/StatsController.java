package gr.teipir.studyrooms.controller;

import gr.teipir.studyrooms.service.BookingService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller για προβολή στατιστικών κρατήσεων.
 * Επιτρέπουμε πρόσβαση μόνο στο προσωπικό της βιβλιοθήκης.
 */
@Controller
public class StatsController {

    // Service για επιχειρησιακή λογική κρατήσεων
    private final BookingService bookingService;

    /**
     * Constructor με dependency injection.
     */
    public StatsController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * GET /stats
     * Εμφανίζουμε τα στατιστικά της ημέρας.
     * Επιτρέπουμε μόνο στο προσωπικό (LIB_STAFF).
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('LIB_STAFF')") // Μόνο Staff
    public String showStats(Model model) {

        // Προσθέτουμε τα στατιστικά στο model
        model.addAttribute("stats", bookingService.getDailyStatistics());

        // Επιστρέφουμε το template stats.html
        return "stats"; // stats.html
    }
}
