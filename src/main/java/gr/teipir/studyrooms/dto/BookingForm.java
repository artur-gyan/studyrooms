package gr.teipir.studyrooms.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Future;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class BookingForm {

    @NotNull(message = "Room ID is required")
    private Long roomId;

    @NotNull(message = "Date is required")
    private LocalDate date;

    @NotNull(message = "Start time is required")
    private LocalTime startTime;

    @NotNull(message = "End time is required")
    private LocalTime endTime;
}