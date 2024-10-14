package com.acme.roombooker.dto;

import com.acme.roombooker.domain.enums.Room;
import com.acme.roombooker.exception.ValidationErrorMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchFiltersDTO {

    @NotNull(message = ValidationErrorMessages.VALIDATION_001_ROOM_IS_REQUIRED)
    private Room room;
    @NotNull(message = ValidationErrorMessages.VALIDATION_004_DATE_IS_REQUIRED)
    private LocalDate bookingDate;

}
