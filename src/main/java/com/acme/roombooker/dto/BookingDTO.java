package com.acme.roombooker.dto;

import com.acme.roombooker.domain.enums.MeetingRoom;
import com.acme.roombooker.exception.ValidationErrorMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingDTO {

    private Long id;

    @NotNull(message = ValidationErrorMessages.ARB1001_ROOM_IS_REQUIRED)
    private MeetingRoom meetingRoom;

    @NotBlank(message = ValidationErrorMessages.ARB1002_MEETING_ORGANIZER_IS_REQUIRED)
    @Email(message = ValidationErrorMessages.ARB1003_INVALID_EMAIL_ADDRESS)
    private String bookedBy;

    @NotNull(message = ValidationErrorMessages.ARB1004_DATE_IS_REQUIRED)
    private LocalDate bookingDate;

    @NotNull(message = ValidationErrorMessages.ARB1005_STARTING_TIME_IS_REQUIRED)
    private LocalTime startTime;

    @NotNull(message = ValidationErrorMessages.ARB1006_END_TIME_IS_REQUIRED)
    private LocalTime endTime;


}
