package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.Booking;
import com.acme.roombooker.dto.BookingDTO;

import java.util.List;

public interface BookingService {

    List<Booking> getAllBookings();

    void addBooking(BookingDTO dto);

    void cancelBooking(Long id);

}
