package gr.teipir.studyrooms.controller.api;

import gr.teipir.studyrooms.model.StudyRoom;
import gr.teipir.studyrooms.service.StudyRoomService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller για τη διαχείριση αιθουσών μελέτης μέσω API.
 * Εδώ χειριζόμαστε προβολή, δημιουργία και ενημέρωση αιθουσών.
 */
@RestController
@RequestMapping("/api/rooms")
public class RoomApiController {

    // Service που περιέχει τη λογική διαχείρισης αιθουσών
    private final StudyRoomService studyRoomService;

    /**
     * Constructor με dependency injection.
     */
    public RoomApiController(StudyRoomService studyRoomService) {
        this.studyRoomService = studyRoomService;
    }

    /**
     * GET /api/rooms
     * Επιστρέφουμε όλες τις αίθουσες.
     * Η πρόσβαση είναι δημόσια.
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAllRooms() {

        /**
         * Ανακτούμε όλες τις αίθουσες από το service.
         */
        List<StudyRoom> rooms = studyRoomService.getAllRooms();

        /**
         * Δημιουργούμε το response.
         */
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", rooms);
        response.put("count", rooms.size());

        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/rooms/{id}
     * Επιστρέφουμε μία αίθουσα με βάση το id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getRoomById(@PathVariable Long id) {
        try {

            /**
             * Ανακτούμε την αίθουσα από το service.
             */
            StudyRoom room = studyRoomService.getRoomById(id);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", room);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            /**
             * Αν δεν βρεθεί η αίθουσα,
             * επιστρέφουμε HTTP 404.
             */
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Room not found");

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error);
        }
    }

    /**
     * POST /api/rooms
     * Δημιουργούμε νέα αίθουσα.
     * Επιτρέπεται μόνο σε χρήστες με ρόλο LIB_STAFF.
     */
    @PostMapping
    @PreAuthorize("hasRole('LIB_STAFF')")
    public ResponseEntity<Map<String, Object>> createRoom(
            @Valid @RequestBody StudyRoom room) {

        /**
         * Αποθηκεύουμε τη νέα αίθουσα.
         */
        StudyRoom savedRoom = studyRoomService.save(room);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Room created successfully");
        response.put("data", savedRoom);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    /**
     * PUT /api/rooms/{id}
     * Ενημερώνουμε υπάρχουσα αίθουσα.
     * Επιτρέπεται μόνο σε χρήστες με ρόλο LIB_STAFF.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('LIB_STAFF')")
    public ResponseEntity<Map<String, Object>> updateRoom(
            @PathVariable Long id,
            @Valid @RequestBody StudyRoom room) {
        try {

            /**
             * Ανακτούμε την υπάρχουσα αίθουσα από τη βάση.
             */
            StudyRoom existingRoom = studyRoomService.getRoomById(id);

            /**
             * Ενημερώνουμε τα πεδία της αίθουσας
             * με τις νέες τιμές που έστειλε ο client.
             */
            existingRoom.setName(room.getName());
            existingRoom.setDescription(room.getDescription());
            existingRoom.setCapacity(room.getCapacity());
            existingRoom.setOpenTime(room.getOpenTime());
            existingRoom.setCloseTime(room.getCloseTime());
            existingRoom.setActive(room.isActive());

            /**
             * Αποθηκεύουμε τις αλλαγές.
             */
            StudyRoom updatedRoom = studyRoomService.save(existingRoom);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Room updated successfully");
            response.put("data", updatedRoom);

            return ResponseEntity.ok(response);

        } catch (Exception e) {

            /**
             * Αν δεν βρεθεί η αίθουσα,
             * επιστρέφουμε HTTP 404.
             */
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", "Room not found");

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(error);
        }
    }
}
