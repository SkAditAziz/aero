package dev.example.aero.dto;

import dev.example.aero.model.Enumaration.SeatClassType;
import jakarta.validation.constraints.AssertTrue;
import lombok.*;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class FlightSearchReqDTO {
    private String fromCode;
    private String toCode;
    @NotNull
    private LocalDate journeyDate;
    private SeatClassType seatClass;
    private int noPassengers;

    @AssertTrue(message = "Cannot fly to the same place!")
    public boolean isFormToCodeDifferent() {
        return !fromCode.equals(toCode);
    }

    public FlightSearchReqDTO(@NotNull LocalDate date) {
        journeyDate = date;
    }
}
