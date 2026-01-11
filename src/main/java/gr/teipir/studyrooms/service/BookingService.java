package gr.teipir.studyrooms.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.DayOfWeek;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;

import gr.teipir.studyrooms.dto.BookingForm;
import gr.teipir.studyrooms.model.*;
import gr.teipir.studyrooms.repository.BookingRepository;
import gr.teipir.studyrooms.repository.StudyRoomRepository;
import gr.teipir.studyrooms.repository.UserRepository;

/**
 * Service για όλες τις λειτουργίες που αφορούν κρατήσεις.
 * Περιλαμβάνει δημιουργία, ακύρωση, έλεγχο ποινών, και στατιστικά.
 */
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final StudyRoomRepository studyRoomRepository;
    private final UserRepository userRepository;
    private final HolidayService holidayService;
    private final NotificationService notificationService;

    public BookingService(BookingRepository bookingRepository,
                          StudyRoomRepository studyRoomRepository,
                          UserRepository userRepository,
                          HolidayService holidayService,
                          NotificationService notificationService) {
        this.bookingRepository = bookingRepository;
        this.studyRoomRepository = studyRoomRepository;
        this.userRepository = userRepository;
        this.holidayService = holidayService;
        this.notificationService = notificationService;
    }

    /**
     * Επιστρέφει όλες τις κρατήσεις.
     */
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    /**
     * Επιστρέφει τις κρατήσεις ενός συγκεκριμένου χρήστη.
     */
    public List<Booking> getBookingsForUser(User user) {
        return bookingRepository.findByStudent(user);
    }

    /**
     * Δημιουργεί νέα κράτηση, με όλους τους απαραίτητους ελέγχους.
     */
    @Transactional
    public void createBooking(BookingForm form) {
        // Λαμβάνουμε τον logged-in χρήστη
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User student = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Logged user not found"));

        // Λαμβάνουμε το δωμάτιο
        StudyRoom room = studyRoomRepository.findById(form.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        // --- Έλεγχος ποινής (No-Show τις τελευταίες 7 μέρες) ---
        LocalDateTime penaltyThreshold = LocalDateTime.now().minusDays(7);
        boolean hasPenalty = bookingRepository.findByStudent(student).stream()
                .filter(b -> b.getStatus() == BookingStatus.NO_SHOW)
                .anyMatch(b -> b.getStartDateTime().isAfter(penaltyThreshold));

        if (hasPenalty) {
            throw new IllegalStateException("You are banned from booking for 7 days due to a recent No-Show!");
        }

        // --- Έλεγχος ενεργού δωματίου ---
        if (!room.isActive()) {
            throw new IllegalStateException("This room is not active");
        }

        LocalDateTime start = LocalDateTime.of(form.getDate(), form.getStartTime());
        LocalDateTime end = LocalDateTime.of(form.getDate(), form.getEndTime());

        // 1. Δεν επιτρέπεται κράτηση στο παρελθόν
        if (start.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Cannot book in the past");
        }

        // 2. Το τέλος πρέπει να είναι μετά την έναρξη
        if (!end.isAfter(start)) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        // 3. Έλεγχος αργιών
        if (holidayService.isHoliday(form.getDate())) {
            throw new IllegalArgumentException("Cannot book on a public holiday!");
        }

        // 4. Έλεγχος ωρών λειτουργίας δωματίου
        LocalTime roomOpen = room.getOpenTime();
        LocalTime roomClose = room.getCloseTime();
        if (form.getStartTime().isBefore(roomOpen) || form.getEndTime().isAfter(roomClose)) {
            throw new IllegalArgumentException(
                    "Room is only available from " + roomOpen + " to " + roomClose
            );
        }

        // 5. Μόνο εργάσιμες μέρες
        DayOfWeek day = form.getDate().getDayOfWeek();
        if (day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY) {
            throw new IllegalArgumentException("Bookings are allowed only on weekdays (Mon-Fri)");
        }

        // 6. Όριο κρατήσεων ανά ημέρα για φοιτητή (max 2)
        LocalDate date = form.getDate();
        long bookingsToday = bookingRepository.findByStudent(student).stream()
                .filter(b -> b.getStartDateTime().toLocalDate().equals(date))
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .count();
        if (bookingsToday >= 2) {
            throw new IllegalArgumentException("You cannot make more than 2 bookings per day");
        }

        // 7. Έλεγχος χωρητικότητας με επικαλυπτόμενες κρατήσεις
        List<Booking> overlappingBookings = bookingRepository.findByStudyRoom(room).stream()
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .filter(b -> start.isBefore(b.getEndDateTime()) && end.isAfter(b.getStartDateTime()))
                .toList();
        if (overlappingBookings.size() >= room.getCapacity()) {
            throw new IllegalArgumentException(
                    "Room is full for this time slot. " +
                            overlappingBookings.size() + "/" + room.getCapacity() + " seats taken"
            );
        }

        // Δημιουργούμε και αποθηκεύουμε την κράτηση
        Booking booking = Booking.builder()
                .student(student)
                .studyRoom(room)
                .startDateTime(start)
                .endDateTime(end)
                .status(BookingStatus.ACTIVE)
                .createdAt(LocalDateTime.now())
                .build();

        bookingRepository.save(booking);

        // Στέλνουμε ειδοποίηση
        notificationService.sendBookingConfirmation(student.getEmail(), room.getName());
    }

    /**
     * Ακυρώνει κράτηση. Φοιτητές μπορούν μόνο τις δικές τους.
     */
    @Transactional
    public void cancelBooking(Long bookingId) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User currentUser = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Logged user not found"));

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        boolean isStaff = currentUser.getRole() == UserRole.LIB_STAFF;
        if (!isStaff && !booking.getStudent().getId().equals(currentUser.getId())) {
            throw new IllegalStateException("You are not allowed to cancel this booking");
        }

        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new IllegalStateException("This booking cannot be cancelled (status: " + booking.getStatus() + ")");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    /**
     * Σημειώνει μια κράτηση ως No-Show (μόνο ACTIVE κρατήσεις)
     */
    @Transactional
    public void markAsNoShow(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (booking.getStatus() != BookingStatus.ACTIVE) {
            throw new IllegalStateException("Cannot mark as No-Show. Status is: " + booking.getStatus());
        }

        booking.setStatus(BookingStatus.NO_SHOW);
        bookingRepository.save(booking);
    }

    /**
     * Επιστρέφει στατιστικά για το dashboard.
     */
    public gr.teipir.studyrooms.dto.StatisticsDto getDailyStatistics() {
        LocalDate today = LocalDate.now();
        List<Booking> allBookings = bookingRepository.findAll();

        long todayCount = allBookings.stream()
                .filter(b -> b.getStartDateTime().toLocalDate().equals(today))
                .filter(b -> b.getStatus() == BookingStatus.ACTIVE)
                .count();

        long cancelledCount = allBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED)
                .count();

        long activeRooms = studyRoomRepository.findAll().stream()
                .filter(StudyRoom::isActive)
                .count();

        String popularRoom = allBookings.stream()
                .map(b -> b.getStudyRoom().getName())
                .reduce((a, b) -> a) // απλοποίηση: παίρνει το πρώτο ή τελευταίο
                .orElse("None yet");

        return gr.teipir.studyrooms.dto.StatisticsDto.builder()
                .totalBookingsToday(todayCount)
                .cancelledBookings(cancelledCount)
                .activeRooms(activeRooms)
                .mostPopularRoom(popularRoom)
                .build();
    }
}


