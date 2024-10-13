package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.Booking;
import com.acme.roombooker.domain.enums.MeetingStatus;
import com.acme.roombooker.domain.repository.BookingRepository;
import com.acme.roombooker.dto.BookingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.acme.roombooker.exception.BookingException;
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
    public Page<BookingDTO> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(this::toBookingDTO);
    }

    @Override
    @Transactional
    public void addBooking(BookingDTO dto) {
        // cannot book room for more than one hour or consecutive multiples of 1 hour (2, 3, 4...)
        // πρέπει ο συνολικός χρόνος σε ώρες να είναι ακέραιος αριθμός
        hasOverlap(dto);

        Booking booking = new Booking();
        booking.setRoom(dto.getRoom());
        booking.setBookedBy(dto.getBookedBy());
        booking.setBookingDate(dto.getBookingDate());
        booking.setStartTime(dto.getStartTime().plusSeconds(1)); // do this to avoid conflicts
        booking.setEndTime(dto.getEndTime());
        booking.setStatus(MeetingStatus.SCHEDULED);

        bookingRepository.save(booking); // 4 test cases
    }

    @Override
    @Transactional
    public void cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessages.B001_BOOKING_NOT_FOUND.name())
        );
        canCancel(booking);

        booking.setStatus(MeetingStatus.CANCELLED);
    }

    @Override
    public Page<BookingDTO> search(SearchFiltersDTO filters, Pageable pageable) {
        return bookingRepository.findAllByRoomAndBookingDate(filters.getRoom(), filters.getBookingDate(), pageable)
                .map(this::toBookingDTO);
    }



    private BookingDTO toBookingDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();
        dto.setId(booking.getId());
        dto.setBookingDate(booking.getBookingDate());
        dto.setRoom(booking.getRoom());
        dto.setBookedBy(booking.getBookedBy());
        dto.setStartTime(booking.getStartTime());
        dto.setEndTime(booking.getEndTime());

        return dto;
    }

    private void canCancel(Booking booking) {
        if (booking.getBookingDate().isBefore(LocalDate.now())) {
            throw new BookingException(ErrorMessages.B002_CANNOT_CANCEL_PAST_BOOKING.name());
        }
    }

    private void hasOverlap(BookingDTO dto) {
        List<Booking> existingBookings = bookingRepository.findAllByRoomAndStartTimeBetween(
                dto.getRoom(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (!existingBookings.isEmpty()) {
            throw new BookingException(ErrorMessages.B003_BOOKING_OVERLAP.name());
        }
    }

}
