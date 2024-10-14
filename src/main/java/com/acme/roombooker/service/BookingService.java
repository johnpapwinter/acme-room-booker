package com.acme.roombooker.service;

import com.acme.roombooker.dto.BookingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    Page<BookingDTO> getAllBookings(Pageable pageable);

    void addBooking(BookingDTO dto);

    void cancelBooking(Long id);

    Page<BookingDTO> search(SearchFiltersDTO filters, Pageable pageable);

    void closeConductedMeetings();

}
