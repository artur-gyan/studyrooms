package gr.teipir.studyrooms.service;

import gr.teipir.studyrooms.dto.PublicHolidayDto;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Service για τον έλεγχο δημόσιων αργιών.
 * Χρησιμοποιεί το Nager.Date API για να επιστρέφει αν μια ημερομηνία είναι αργία στην Ελλάδα.
 */
@Service
public class HolidayService {

    private final RestTemplate restTemplate;
    private static final String API_URL = "https://date.nager.at/api/v3/publicholidays/{year}/{countryCode}";

    public HolidayService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Ελέγχει αν η ημερομηνία είναι δημόσια αργία στην Ελλάδα.
     * @param date Η ημερομηνία προς έλεγχο
     * @return true αν είναι αργία, false αλλιώς
     */
    public boolean isHoliday(LocalDate date) {
        try {
            int year = date.getYear();
            String url = API_URL.replace("{year}", String.valueOf(year))
                    .replace("{countryCode}", "GR");

            ResponseEntity<PublicHolidayDto[]> response =
                    restTemplate.getForEntity(url, PublicHolidayDto[].class);

            if (response.getBody() != null) {
                List<PublicHolidayDto> holidays = Arrays.asList(response.getBody());
                return holidays.stream()
                        .anyMatch(h -> h.getDate().equals(date));
            }
        } catch (Exception e) {
            // Αν υπάρξει πρόβλημα με το API, καταγράφουμε και επιστρέφουμε false για fail-safe
            System.err.println("Error calling Public Holidays API: " + e.getMessage());
            return false;
        }

        return false;
    }
}
