package gr.teipir.studyrooms.repository;

import gr.teipir.studyrooms.model.Booking;
import gr.teipir.studyrooms.model.User;
import gr.teipir.studyrooms.model.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository για την οντότητα Booking.
 * Παρέχει πρόσβαση στη βάση δεδομένων και query methods για κρατήσεις.
 */
public interface BookingRepository extends JpaRepository<Booking, Long> {

    /**
     * Βρίσκουμε όλες τις κρατήσεις που ανήκουν σε συγκεκριμένο φοιτητή.
     * @param student ο χρήστης (φοιτητής)
     * @return λίστα κρατήσεων
     */
    List<Booking> findByStudent(User student);

    /**
     * Βρίσκουμε όλες τις κρατήσεις για συγκεκριμένη αίθουσα.
     * @param studyRoom η αίθουσα
     * @return λίστα κρατήσεων
     */
    List<Booking> findByStudyRoom(StudyRoom studyRoom);
}



