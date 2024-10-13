package com.acme.roombooker.dto;

import com.acme.roombooker.domain.enums.Room;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingDTO {

    private Long id;

    @NotNull
    private Room room;

    @NotBlank
    @Email
    private String bookedBy;

    @NotNull
    private LocalDate bookingDate;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;


}
