package com.acme.roombooker.controller;

import com.acme.roombooker.dto.BookingDTO;
import com.acme.roombooker.service.BookingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }


    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings() {
        return ResponseEntity.ok(bookingService.getAllBookings());
    }


    @PostMapping("/book")
    public ResponseEntity<Void> bookMeeting(@RequestBody BookingDTO dto) {
        bookingService.addBooking(dto);

        return ResponseEntity.ok().build();
    }


}
