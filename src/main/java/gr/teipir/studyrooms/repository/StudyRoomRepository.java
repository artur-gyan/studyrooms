package gr.teipir.studyrooms.repository;

import gr.teipir.studyrooms.model.StudyRoom;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Repository για την οντότητα StudyRoom.
 * Παρέχει πρόσβαση στη βάση δεδομένων και βασικές CRUD λειτουργίες για τις αίθουσες.
 */
public interface StudyRoomRepository extends JpaRepository<StudyRoom, Long> {
    // Δεν χρειάζεται επιπλέον μέθοδος, οι βασικές CRUD λειτουργίες παρέχονται από το JpaRepository
}

