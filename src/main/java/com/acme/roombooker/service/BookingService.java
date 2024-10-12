package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.Booking;
import com.acme.roombooker.dto.BookingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    Page<Booking> getAllBookings(Pageable pageable);

    void addBooking(BookingDTO dto);

    void cancelBooking(Long id);

    Page<Booking> search(SearchFiltersDTO filters, Pageable pageable);

}
