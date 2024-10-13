package com.acme.roombooker.controller;

import com.acme.roombooker.dto.BookingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.acme.roombooker.service.BookingService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @GetMapping("/get-all")
    public ResponseEntity<Page<BookingDTO>> getAllBookings(Pageable pageable) {
        Page<BookingDTO> response = bookingService.getAllBookings(pageable);

        return ResponseEntity.ok(response);
    }


    @PostMapping("/book")
    public ResponseEntity<Void> bookMeeting(@Valid @RequestBody BookingDTO dto) {
        bookingService.addBooking(dto);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/cancel/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable Long id) {
        bookingService.cancelBooking(id);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/search")
    public ResponseEntity<Page<BookingDTO>> searchBookings(@Valid @RequestBody SearchFiltersDTO filters,
                                                           Pageable pageable) {
        Page<BookingDTO> response = bookingService.search(filters, pageable);

        return ResponseEntity.ok(response);
    }

}
