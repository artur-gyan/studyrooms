package gr.teipir.studyrooms.service;

import gr.teipir.studyrooms.model.StudyRoom;
import gr.teipir.studyrooms.repository.StudyRoomRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

/**
 * Service για διαχείριση αιθουσών μελέτης.
 */
@Service
public class StudyRoomService {

    private final StudyRoomRepository studyRoomRepository;

    public StudyRoomService(StudyRoomRepository studyRoomRepository) {
        this.studyRoomRepository = studyRoomRepository;
    }

    /**
     * Επιστρέφει μία αίθουσα με βάση το ID.
     * @param id Το ID της αίθουσας
     * @return StudyRoom
     * @throws NoSuchElementException Αν η αίθουσα δεν υπάρχει
     */
    public StudyRoom getRoomById(Long id) {
        return studyRoomRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Room not found"));
    }

    /**
     * Επιστρέφει όλες τις αίθουσες.
     * @return Λίστα με StudyRoom
     */
    public List<StudyRoom> getAllRooms() {
        return studyRoomRepository.findAll();
    }

    /**
     * Αποθηκεύει ή ενημερώνει μία αίθουσα.
     * @param room Το αντικείμενο StudyRoom
     * @return Το αποθηκευμένο αντικείμενο
     */
    public StudyRoom save(StudyRoom room) {
        return studyRoomRepository.save(room);
    }
}

