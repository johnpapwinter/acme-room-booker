package com.acme.roombooker.dto;

import com.acme.roombooker.domain.enums.MeetingRoom;
import com.acme.roombooker.exception.ErrorMessages;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchFiltersDTO {

    @NotNull(message = ErrorMessages.ARB1001_ROOM_IS_REQUIRED)
    private MeetingRoom meetingRoom;
    @NotNull(message = ErrorMessages.ARB1004_DATE_IS_REQUIRED)
    private LocalDate bookingDate;

}
