package com.acme.roombooker.dto;

import com.acme.roombooker.domain.enums.MeetingRoom;
import com.acme.roombooker.exception.ValidationErrorMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchFiltersDTO {

    @NotNull(message = ValidationErrorMessages.ARB1001_ROOM_IS_REQUIRED)
    private MeetingRoom meetingRoom;
    @NotNull(message = ValidationErrorMessages.ARB1004_DATE_IS_REQUIRED)
    private LocalDate bookingDate;

}
