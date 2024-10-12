package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.Booking;
import com.acme.roombooker.domain.enums.MeetingStatus;
import com.acme.roombooker.domain.repository.BookingRepository;
import com.acme.roombooker.dto.BookingDTO;
import com.acme.roombooker.exception.BookingException;
import com.acme.roombooker.exception.ErrorMessages;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;


    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }


    @Override
    public List<Booking> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    @Transactional
    public void addBooking(BookingDTO dto) {
        Booking booking = new Booking();
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
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new BookingException(ErrorMessages.B001_BOOKING_NOT_FOUND.name())
        );

        booking.setStatus(MeetingStatus.CANCELLED);
    }

}
