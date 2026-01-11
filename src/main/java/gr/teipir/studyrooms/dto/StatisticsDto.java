package gr.teipir.studyrooms.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class StatisticsDto {
    private long totalBookingsToday;
    private long activeRooms;
    private long cancelledBookings;
    private String mostPopularRoom;
}