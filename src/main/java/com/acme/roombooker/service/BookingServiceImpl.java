package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.Booking;
import com.acme.roombooker.domain.enums.MeetingStatus;
import com.acme.roombooker.domain.repository.BookingRepository;
import com.acme.roombooker.dto.BookingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.acme.roombooker.exception.EntityNotFoundException;
import com.acme.roombooker.exception.ErrorMessages;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;


    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }


    @Override
    public Page<Booking> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable);
    }

    @Override
    @Transactional
    public void addBooking(BookingDTO dto) {
        // cannot book room for more than one hour or consecutive multiples of 1 hour (2, 3, 4...)
        // bookings cannot overlap

        Booking booking = new Booking();
        booking.setRoom(dto.getRoom());
        booking.setBookedBy(dto.getBookedBy());
        booking.setBookingDate(dto.getBookingDate());
        booking.setStartTime(dto.getStartTime());
        booking.setEndTime(dto.getEndTime());
        booking.setStatus(MeetingStatus.SCHEDULED);

        bookingRepository.save(booking);
    }

    @Override
    @Transactional
    public void cancelBooking(Long id) {
        // cannot cancel a booking from a previous date
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessages.B001_BOOKING_NOT_FOUND.name())
        );

        if (booking.getBookingDate().isBefore(LocalDate.now())) {
            throw new EntityNotFoundException(ErrorMessages.B001_BOOKING_NOT_FOUND.name());
        }

        booking.setStatus(MeetingStatus.CANCELLED);
    }

    @Override
    public Page<Booking> search(SearchFiltersDTO filters, Pageable pageable) {
        return bookingRepository.findAllByRoomAndBookingDate(filters.getRoom(), filters.getBookingDate(), pageable);
    }

}
