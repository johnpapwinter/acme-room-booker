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

import java.time.Duration;
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
        isTimeRounded(dto);
        isDurationValid(dto);
        hasOverlap(dto);

        Booking booking = new Booking();
        booking.setRoom(dto.getRoom());
        booking.setBookedBy(dto.getBookedBy());
        booking.setBookingDate(dto.getBookingDate());
        // since bookings are always from the top of the hour or half-hours and in order to avoid
        // overlap errors, we add 1 second to the start time, it could be nanos, but seconds will do in this case
        booking.setStartTime(dto.getStartTime().plusSeconds(1));
        booking.setEndTime(dto.getEndTime());
        booking.setStatus(MeetingStatus.SCHEDULED);

        bookingRepository.save(booking);
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

    private void isDurationValid(BookingDTO dto) {
        Duration duration = Duration.between(dto.getStartTime(), dto.getEndTime());

        if (duration.toMinutes() < 60 || duration.toMinutes() % 60 != 0) {
            throw new BookingException(ErrorMessages.B005_MEETING_DURATION_IS_NOT_VALID.name());
        }
    }

    private void isTimeRounded(BookingDTO dto) {
        boolean startingTime = dto.getStartTime().getMinute() == 0 | dto.getStartTime().getMinute() == 30;
        boolean endingTime = dto.getEndTime().getMinute() == 0 | dto.getEndTime().getMinute() == 30;

        if (!(startingTime && endingTime)) {
            throw new BookingException(ErrorMessages.B004_MEETING_TIME_NOT_ROUNDED.name());
        }
    }

    private void hasOverlap(BookingDTO dto) {
        List<Booking> existingBookings = bookingRepository.findAllByRoomAndBookingDateAndStartTimeBetween(
                dto.getRoom(),
                dto.getBookingDate(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (!existingBookings.isEmpty()) {
            throw new BookingException(ErrorMessages.B003_BOOKING_OVERLAP.name());
        }
    }

}
