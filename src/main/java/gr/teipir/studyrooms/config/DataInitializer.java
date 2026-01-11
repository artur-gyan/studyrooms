package gr.teipir.studyrooms.config;

import gr.teipir.studyrooms.model.*;
import gr.teipir.studyrooms.repository.StudyRoomRepository;
import gr.teipir.studyrooms.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

/**
 * Κλάση αρχικοποίησης δεδομένων.
 * Τη χρησιμοποιούμε για να εισάγουμε αρχικά δεδομένα στη βάση
 * κατά την εκκίνηση της εφαρμογής, εφόσον η βάση είναι άδεια.
 */
@Component
public class DataInitializer {

    // Repository για τη διαχείριση των study rooms
    private final StudyRoomRepository studyRoomRepository;

    // Repository για τη διαχείριση των χρηστών
    private final UserRepository userRepository;

    // Encoder για την ασφαλή αποθήκευση των κωδικών πρόσβασης
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor με dependency injection.
     * Το Spring μας παρέχει αυτόματα τα απαραίτητα beans.
     */
    public DataInitializer(StudyRoomRepository studyRoomRepository,
                           UserRepository userRepository,
                           PasswordEncoder passwordEncoder) {
        this.studyRoomRepository = studyRoomRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Η μέθοδος αυτή εκτελείται αυτόματα μετά τη δημιουργία του bean.
     * Τη χρησιμοποιούμε για αρχικοποίηση δεδομένων στη βάση.
     */
    @PostConstruct
    public void init() {

        /**
         * Ελέγχουμε αν δεν υπάρχουν study rooms στη βάση.
         * Αν η βάση είναι άδεια, δημιουργούμε προκαθορισμένες αίθουσες.
         */
        if (studyRoomRepository.count() == 0) {

            // Δημιουργούμε και αποθηκεύουμε την αίθουσα Room A
            studyRoomRepository.save(new StudyRoom(
                    null,
                    "Room A",
                    "Quiet room",
                    10,
                    LocalTime.of(8, 0),
                    LocalTime.of(20, 0),
                    true
            ));

            // Δημιουργούμε και αποθηκεύουμε την αίθουσα Room B
            studyRoomRepository.save(new StudyRoom(
                    null,
                    "Room B",
                    "Group work room",
                    6,
                    LocalTime.of(9, 0),
                    LocalTime.of(18, 0),
                    true
            ));
        }

        /**
         * Ελέγχουμε αν δεν υπάρχουν χρήστες στη βάση.
         * Αν η βάση είναι άδεια, δημιουργούμε προκαθορισμένους χρήστες.
         */
        if (userRepository.count() == 0) {

            // Δημιουργούμε χρήστη με ρόλο STUDENT
            User student = User.builder()
                    .username("student1")
                    // Κωδικοποιούμε τον κωδικό πριν την αποθήκευση
                    .password(passwordEncoder.encode("1234"))
                    .fullName("Student One")
                    .email("student1@example.com")
                    .role(UserRole.STUDENT)
                    .build();

            // Δημιουργούμε χρήστη με ρόλο LIB_STAFF
            User staff = User.builder()
                    .username("staff1")
                    // Κωδικοποιούμε τον κωδικό πριν την αποθήκευση
                    .password(passwordEncoder.encode("1234"))
                    .fullName("Library Staff")
                    .email("staff1@example.com")
                    .role(UserRole.LIB_STAFF)
                    .build();

            // Αποθηκεύουμε τους χρήστες στη βάση
            userRepository.save(student);
            userRepository.save(staff);
        }
    }
}




