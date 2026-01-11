package gr.teipir.studyrooms.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;

/**
 * Οντότητα για τις αίθουσες μελέτης.
 * Αντιπροσωπεύει μια φυσική αίθουσα με όλα τα χαρακτηριστικά της.
 */
@Entity
@Table(name = "study_rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudyRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Μοναδικό ID της αίθουσας (primary key)

    @Column(nullable = false, unique = true)
    private String name; // Όνομα αίθουσας, υποχρεωτικό και μοναδικό

    private String description; // Περιγραφή της αίθουσας (προαιρετική)

    @Column(nullable = false)
    private int capacity; // Χωρητικότητα αίθουσας, υποχρεωτικό

    @Column(nullable = false)
    private LocalTime openTime; // Ώρα έναρξης λειτουργίας αίθουσας

    @Column(nullable = false)
    private LocalTime closeTime; // Ώρα λήξης λειτουργίας αίθουσας

    @Column(nullable = false)
    private boolean active = true; // Κατάσταση αίθουσας (ενεργή ή όχι), default true
}

