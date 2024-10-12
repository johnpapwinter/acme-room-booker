package com.acme.roombooker.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingDTO {

    private Long id;
    private String bookedBy;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;


}
