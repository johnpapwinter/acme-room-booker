package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.Booking;
import com.acme.roombooker.domain.enums.MeetingStatus;
import com.acme.roombooker.domain.repository.BookingRepository;
import com.acme.roombooker.dto.BookingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.acme.roombooker.exception.BookingException;
import com.acme.roombooker.exception.EntityNotFoundException;
import com.acme.roombooker.exception.ErrorMessages;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {

    private static final Logger logger = LoggerFactory.getLogger(BookingServiceImpl.class);

    private final BookingRepository bookingRepository;


    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    /**
     * Takes a Spring Pageable object and returns the bookings in a paginated form.
     * @param pageable The Pageable object containing pagination information.
     * @return A Page of BookingDTO objects.
     */
    @Override
    public Page<BookingDTO> getAllBookings(Pageable pageable) {
        return bookingRepository.findAll(pageable)
                .map(this::toBookingDTO);
    }

    /**
     * Adds a new booking based on the provided BookingDTO.
     * @param dto The BookingDTO object containing the booking details.
     * @return The ID of the newly created booking.
     */
    @Override
    @Transactional
    public Long addBooking(BookingDTO dto) {
        isTimeRounded(dto);
        isDurationValid(dto);
        hasOverlap(dto);
        hasConflictingBooking(dto);

        Booking booking = new Booking();
        booking.setRoom(dto.getRoom());
        booking.setBookedBy(dto.getBookedBy());
        booking.setBookingDate(dto.getBookingDate());
        // since bookings are always on the top of the hour or half-hours and in order to avoid
        // overlap errors, we add 1 second to the start time, it could be nanos, but seconds will do in this case
        booking.setStartTime(dto.getStartTime().plusSeconds(1));
        booking.setEndTime(dto.getEndTime());
        booking.setStatus(MeetingStatus.SCHEDULED);

        bookingRepository.save(booking);

        logger.info("CREATED BOOKING WITH ID:{}", booking.getId());
        return booking.getId();
    }

    /**
     * Cancels a booking by its ID.
     * @param id The ID of the booking to be cancelled.
     * @return The BookingDTO object representing the cancelled booking.
     * @throws EntityNotFoundException if the booking with the given ID is not found.
     */
    @Override
    @Transactional
    public BookingDTO cancelBooking(Long id) {
        Booking booking = bookingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessages.ARB_001_BOOKING_NOT_FOUND.name())
        );
        canCancel(booking);

        booking.setStatus(MeetingStatus.CANCELLED);

        logger.info("CANCELLED BOOKING WITH ID:{}", booking.getId());
        return toBookingDTO(booking);
    }

    /**
     * Searches for bookings based on the provided search filters and pagination information.
     * @param filters The SearchFiltersDTO object containing the search criteria.
     * @param pageable The Pageable object containing pagination information.
     * @return A Page of BookingDTO objects matching the search criteria.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<BookingDTO> search(SearchFiltersDTO filters, Pageable pageable) {
        return bookingRepository.findAllByRoomAndBookingDate(filters.getRoom(), filters.getBookingDate(), pageable)
                .map(this::toBookingDTO);
    }

    /**
     * Closes all conducted meetings by setting their status to "COMPLETED".
     */
    @Override
    @Transactional
    public void closeConductedMeetings() {
        List<Booking> pastBookings = bookingRepository.findAllByBookingDateBeforeAndStatus(LocalDate.now(), MeetingStatus.SCHEDULED);
        pastBookings.forEach(booking -> booking.setStatus(MeetingStatus.COMPLETED));
        logger.info("CLOSED {} PAST BOOKINGS", pastBookings.size());
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
        if (!booking.getStatus().equals(MeetingStatus.SCHEDULED)) {
//        if (booking.getBookingDate().isBefore(LocalDate.now())) {
            throw new BookingException(ErrorMessages.ARB_002_CANNOT_CANCEL_PAST_BOOKING.name());
        }
    }

    private void hasConflictingBooking(BookingDTO dto) {
        bookingRepository.findBookingByBookedByAndBookingDateAndStartTimeAfterAndEndTimeBefore(
                dto.getBookedBy(), dto.getBookingDate(), dto.getStartTime(), dto.getEndTime()
        ).ifPresent(
                value -> {
                    throw new BookingException(ErrorMessages.ARB_007_HAS_A_CONFLICTING_BOOKING.name());
                }
        );
    }

    private void isDurationValid(BookingDTO dto) {
        Duration duration = Duration.between(dto.getStartTime(), dto.getEndTime());

        if (duration.toMinutes() < 60 || duration.toMinutes() % 60 != 0) {
            throw new BookingException(ErrorMessages.ARB_005_MEETING_DURATION_IS_NOT_VALID.name());
        }
    }

    private void isTimeRounded(BookingDTO dto) {
        boolean startingTime = dto.getStartTime().getMinute() == 0 | dto.getStartTime().getMinute() == 30;
        boolean endingTime = dto.getEndTime().getMinute() == 0 | dto.getEndTime().getMinute() == 30;

        if (!(startingTime && endingTime)) {
            throw new BookingException(ErrorMessages.ARB_004_MEETING_TIME_NOT_ROUNDED.name());
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
            throw new BookingException(ErrorMessages.ARB_003_BOOKING_OVERLAP.name());
        }
    }

}
