package gr.teipir.studyrooms.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Οντότητα για τους χρήστες της εφαρμογής.
 * Αντιπροσωπεύει κάθε εγγεγραμμένο χρήστη με τα στοιχεία του και τον ρόλο του.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Μοναδικό ID χρήστη (primary key)

    @Column(nullable = false, unique = true)
    private String username; // Όνομα χρήστη, υποχρεωτικό και μοναδικό

    @Column(nullable = false)
    private String password; // Κωδικός πρόσβασης, υποχρεωτικός

    @Column(nullable = false)
    private String fullName; // Πλήρες όνομα χρήστη, υποχρεωτικό

    @Column(nullable = false, unique = true)
    private String email; // Email χρήστη, υποχρεωτικό και μοναδικό

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role; // Ρόλος χρήστη (STUDENT, LIB_STAFF), αποθηκεύεται ως String
}



