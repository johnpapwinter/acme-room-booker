package com.acme.roombooker.dto;

import com.acme.roombooker.domain.enums.Room;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class BookingDTO {

    private Long id;
    private Room room;
    private String bookedBy;
    private LocalDate bookingDate;
    private LocalTime startTime;
    private LocalTime endTime;


}
