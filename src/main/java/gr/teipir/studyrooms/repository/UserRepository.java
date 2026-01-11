package gr.teipir.studyrooms.repository;

import gr.teipir.studyrooms.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Repository για την οντότητα User.
 * Παρέχει πρόσβαση στη βάση δεδομένων και μεθόδους για αναζήτηση χρηστών.
 */
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Βρίσκουμε έναν χρήστη με βάση το username.
     * @param username το όνομα χρήστη
     * @return Optional<User> με τον χρήστη αν υπάρχει, αλλιώς κενό
     */
    Optional<User> findByUsername(String username);
}


