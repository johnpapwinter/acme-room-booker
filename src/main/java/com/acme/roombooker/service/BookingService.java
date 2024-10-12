package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.Booking;
import com.acme.roombooker.dto.BookingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;

import java.util.List;

public interface BookingService {

    List<Booking> getAllBookings();

    void addBooking(BookingDTO dto);

    void cancelBooking(Long id);

    List<Booking> search(SearchFiltersDTO filters);

}
