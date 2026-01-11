package gr.teipir.studyrooms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicHolidayDto {
    private LocalDate date;
    private String localName;
    private String name;
}