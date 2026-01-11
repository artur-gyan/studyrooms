package gr.teipir.studyrooms.controller;

import gr.teipir.studyrooms.model.StudyRoom;
import gr.teipir.studyrooms.service.StudyRoomService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Controller για το Web UI των αιθουσών μελέτης.
 * Εδώ χειριζόμαστε την προβολή, δημιουργία και αποθήκευση αιθουσών.
 */
@Controller
public class StudyRoomController {

    // Service για επιχειρησιακή λογική αιθουσών
    private final StudyRoomService studyRoomService;

    /**
     * Constructor με dependency injection.
     */
    public StudyRoomController(StudyRoomService studyRoomService) {
        this.studyRoomService = studyRoomService;
    }

    /**
     * GET /rooms
     * Προβολή όλων των αιθουσών.
     */
    @GetMapping("/rooms")
    public String rooms(Model model) {

        // Προσθέτουμε όλες τις αίθουσες στο model
        model.addAttribute("rooms", studyRoomService.getAllRooms());

        // Επιστρέφουμε το template rooms.html
        return "rooms";  // rooms.html
    }

    /**
     * GET /rooms/new
     * Εμφανίζουμε τη φόρμα δημιουργίας νέας αίθουσας.
     */
    @GetMapping("/rooms/new")
    public String showCreateForm(Model model) {

        // Προσθέτουμε ένα νέο StudyRoom στο model για τη φόρμα
        model.addAttribute("room", new StudyRoom());

        // Επιστρέφουμε το template room-form.html
        return "room-form";
    }

    /**
     * POST /rooms
     * Δημιουργούμε και αποθηκεύουμε νέα αίθουσα.
     */
    @PostMapping("/rooms")
    public String createRoom(StudyRoom room) {

        // Αποθηκεύουμε τη νέα αίθουσα μέσω του service
        studyRoomService.save(room);

        // Μετά την αποθήκευση, κάνουμε redirect στη λίστα αιθουσών
        return "redirect:/rooms";
    }
}


