package com.acme.roombooker.controller;

import com.acme.roombooker.dto.MeetingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.acme.roombooker.security.AcmePrincipal;
import com.acme.roombooker.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/meetings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @GetMapping("/get-all")
    public ResponseEntity<Page<MeetingDTO>> getAllBookings(Pageable pageable) {
        Page<MeetingDTO> response = bookingService.getAllBookings(pageable);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/book")
    public ResponseEntity<Long> bookMeeting(@Valid @RequestBody MeetingDTO dto,
                                            @AuthenticationPrincipal AcmePrincipal acmePrincipal) {
        Long result = bookingService.addBooking(dto, acmePrincipal);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/cancel/{id}")
    public ResponseEntity<MeetingDTO> cancelBooking(@PathVariable Long id) {
        MeetingDTO result = bookingService.cancelBooking(id);

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/search")
    public ResponseEntity<Page<MeetingDTO>> searchBookings(@Valid @RequestBody SearchFiltersDTO filters,
                                                           Pageable pageable) {
        Page<MeetingDTO> response = bookingService.search(filters, pageable);

        return ResponseEntity.ok(response);
    }

}
