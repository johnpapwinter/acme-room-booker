package com.acme.roombooker.dto;

import com.acme.roombooker.enums.MeetingRoom;
import com.acme.roombooker.exception.ErrorMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Builder
public class MeetingDTO {

    private Long id;

    @NotNull(message = ErrorMessages.ARB1001_ROOM_IS_REQUIRED)
    private MeetingRoom meetingRoom;

    @NotBlank(message = ErrorMessages.ARB1002_MEETING_ORGANIZER_IS_REQUIRED)
    @Email(message = ErrorMessages.ARB1003_INVALID_EMAIL_ADDRESS)
    private String bookedBy;

    @NotNull(message = ErrorMessages.ARB1004_DATE_IS_REQUIRED)
    private LocalDate bookingDate;

    @NotNull(message = ErrorMessages.ARB1005_STARTING_TIME_IS_REQUIRED)
    private LocalTime startTime;

    @NotNull(message = ErrorMessages.ARB1006_END_TIME_IS_REQUIRED)
    private LocalTime endTime;


}
