package com.acme.roombooker.service;

import com.acme.roombooker.dto.MeetingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.acme.roombooker.security.AcmePrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    Page<MeetingDTO> getAllBookings(Pageable pageable);

    Long addBooking(MeetingDTO dto, AcmePrincipal acmePrincipal);

    MeetingDTO cancelBooking(Long id);

    Page<MeetingDTO> search(SearchFiltersDTO filters, Pageable pageable);

    void closeConductedMeetings();

}
