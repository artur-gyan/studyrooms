package gr.teipir.studyrooms.controller;

import gr.teipir.studyrooms.dto.BookingForm;
import gr.teipir.studyrooms.model.StudyRoom;
import gr.teipir.studyrooms.model.User;
import gr.teipir.studyrooms.model.UserRole;
import gr.teipir.studyrooms.repository.UserRepository;
import gr.teipir.studyrooms.service.BookingService;
import gr.teipir.studyrooms.service.StudyRoomService;
import jakarta.validation.Valid;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * Controller για το Web UI των κρατήσεων.
 * Εδώ χειριζόμαστε την προβολή, δημιουργία, ακύρωση και σήμανση "no-show" κρατήσεων.
 */
@Controller
public class BookingController {

    // Service για επιχειρησιακή λογική κρατήσεων
    private final BookingService bookingService;

    // Service για διαχείριση αιθουσών
    private final StudyRoomService studyRoomService;

    // Repository για ανάκτηση χρηστών
    private final UserRepository userRepository;

    /**
     * Constructor με dependency injection.
     */
    public BookingController(BookingService bookingService,
                             StudyRoomService studyRoomService,
                             UserRepository userRepository) {
        this.bookingService = bookingService;
        this.studyRoomService = studyRoomService;
        this.userRepository = userRepository;
    }

    /**
     * GET /bookings
     * Προβολή λίστας κρατήσεων.
     * - Το προσωπικό βλέπει όλες τις κρατήσεις
     * - Οι φοιτητές βλέπουν μόνο τις δικές τους
     */
    @GetMapping("/bookings")
    public String listBookings(Model model) {

        // Ανακτούμε τον authenticated χρήστη
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Logged user not found"));

        model.addAttribute("currentUser", user);
        model.addAttribute("isStaff", user.getRole() == UserRole.LIB_STAFF);

        // Φορτώνουμε τις κατάλληλες κρατήσεις ανάλογα με το ρόλο
        if (user.getRole() == UserRole.LIB_STAFF) {
            model.addAttribute("bookings", bookingService.getAllBookings());
        } else {
            model.addAttribute("bookings", bookingService.getBookingsForUser(user));
        }

        return "bookings";
    }

    /**
     * GET /bookings/new?roomId={id}
     * Εμφανίζουμε τη φόρμα δημιουργίας νέας κράτησης για συγκεκριμένη αίθουσα.
     */
    @GetMapping("/bookings/new")
    public String showBookingForm(@RequestParam("roomId") Long roomId, Model model) {

        // Ανακτούμε την αίθουσα από το service
        StudyRoom room = studyRoomService.getRoomById(roomId);

        // Δημιουργούμε νέο BookingForm και ορίζουμε το roomId
        BookingForm form = new BookingForm();
        form.setRoomId(roomId);

        // Προσθέτουμε την αίθουσα και το form στο model
        model.addAttribute("room", room);
        model.addAttribute("bookingForm", form);

        return "booking-form";
    }

    /**
     * POST /bookings
     * Δημιουργούμε νέα κράτηση.
     */
    @PostMapping("/bookings")
    public String createBooking(@Valid @ModelAttribute("bookingForm") BookingForm form,
                                BindingResult bindingResult,
                                Model model) {

        /**
         * Αν υπάρχουν validation errors, εμφανίζουμε ξανά τη φόρμα με την αίθουσα.
         */
        if (bindingResult.hasErrors()) {
            StudyRoom room = studyRoomService.getRoomById(form.getRoomId());
            model.addAttribute("room", room);
            return "booking-form";
        }

        try {
            // Δημιουργούμε την κράτηση μέσω του service
            bookingService.createBooking(form);
            return "redirect:/bookings";
        } catch (IllegalArgumentException | IllegalStateException ex) {
            // Αν γίνει σφάλμα, εμφανίζουμε ξανά τη φόρμα με μήνυμα λάθους
            StudyRoom room = studyRoomService.getRoomById(form.getRoomId());
            model.addAttribute("room", room);
            model.addAttribute("errorMessage", ex.getMessage());
            return "booking-form";
        }
    }

    /**
     * POST /bookings/{id}/cancel
     * Ακυρώνουμε κράτηση.
     */
    @PostMapping("/bookings/{id}/cancel")
    public String cancelBooking(@PathVariable("id") Long id, Model model) {
        try {
            bookingService.cancelBooking(id);
            return "redirect:/bookings";
        } catch (IllegalStateException | IllegalArgumentException ex) {
            // Σε περίπτωση σφάλματος, προσθέτουμε μήνυμα λάθους
            model.addAttribute("errorMessage", ex.getMessage());
            return "redirect:/bookings?error=" + ex.getMessage();
        }
    }

    /**
     * POST /bookings/{id}/noshow
     * Σημαίνουμε μία κράτηση ως "no-show".
     * Επιτρέπεται μόνο στο προσωπικό (LIB_STAFF).
     */
    @PostMapping("/bookings/{id}/noshow")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('LIB_STAFF')")
    public String markAsNoShow(@PathVariable("id") Long id, Model model) {
        try {
            bookingService.markAsNoShow(id);
            return "redirect:/bookings";
        } catch (IllegalStateException | IllegalArgumentException ex) {
            // Αν γίνει σφάλμα, το εμφανίζουμε μέσω URL (απλοποιημένο)
            return "redirect:/bookings?error=" + ex.getMessage();
        }
    }
}
