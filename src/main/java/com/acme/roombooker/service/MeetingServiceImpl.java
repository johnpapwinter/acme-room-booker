package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.AcmeUser;
import com.acme.roombooker.domain.entity.Meeting;
import com.acme.roombooker.domain.enums.MeetingStatus;
import com.acme.roombooker.domain.repository.MeetingRepository;
import com.acme.roombooker.dto.MeetingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.acme.roombooker.exception.BookingException;
import com.acme.roombooker.exception.EntityNotFoundException;
import com.acme.roombooker.exception.ErrorMessages;
import com.acme.roombooker.security.AcmePrincipal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final AcmeUserService acmeUserService;


    public MeetingServiceImpl(MeetingRepository meetingRepository, AcmeUserService acmeUserService) {
        this.meetingRepository = meetingRepository;
        this.acmeUserService = acmeUserService;
    }

    /**
     * Takes a Spring Pageable object and returns the bookings in a paginated form.
     * @param pageable The Pageable object containing pagination information.
     * @return A Page of BookingDTO objects.
     */
    @Override
    public Page<MeetingDTO> getAllMeetings(Pageable pageable) {
        return meetingRepository.findAll(pageable)
                .map(this::toMeetingDTO);
    }

    /**
     * Adds a new booking based on the provided BookingDTO.
     * @param dto The BookingDTO object containing the booking details.
     * @param acmePrincipal The current user accessing the method
     * @return The ID of the newly created booking.
     */
    @Override
    @Transactional
    public Long addMeeting(MeetingDTO dto, AcmePrincipal acmePrincipal) {
        isTimeRounded(dto);
        isDurationValid(dto);
        hasOverlap(dto);
        hasConflictingBooking(dto);

        AcmeUser acmeUser = acmeUserService.findAcmeUserByUsername(acmePrincipal.getUsername());

        Meeting meeting = new Meeting();
        meeting.setMeetingRoom(dto.getMeetingRoom());
        meeting.setBookedBy(acmeUser.getEmail());
        meeting.setBookingDate(dto.getBookingDate());
        // since bookings are always on the top of the hour or half-hours and in order to avoid
        // overlap errors, we add 1 second to the start time, it could be nanos, but seconds will do in this case
        meeting.setStartTime(dto.getStartTime().plusSeconds(1));
        meeting.setEndTime(dto.getEndTime());
        meeting.setStatus(MeetingStatus.SCHEDULED);
        meeting.setAcmeUser(acmeUser);
        acmeUser.getMeetings().add(meeting);

        meetingRepository.save(meeting);

        return meeting.getId();
    }

    /**
     * Cancels a booking by its ID.
     * @param id The ID of the booking to be cancelled.
     * @return The BookingDTO object representing the cancelled booking.
     * @throws EntityNotFoundException if the booking with the given ID is not found.
     */
    @Override
    @Transactional
    public MeetingDTO cancelMeeting(Long id) {
        Meeting meeting = meetingRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException(ErrorMessages.ARB_001_BOOKING_NOT_FOUND)
        );
        canCancel(meeting);

        meeting.setStatus(MeetingStatus.CANCELLED);

        return toMeetingDTO(meeting);
    }

    /**
     * Searches for bookings based on the provided search filters and pagination information.
     * @param filters The SearchFiltersDTO object containing the search criteria.
     * @param pageable The Pageable object containing pagination information.
     * @return A Page of BookingDTO objects matching the search criteria.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<MeetingDTO> search(SearchFiltersDTO filters, Pageable pageable) {
        return meetingRepository.findAllByMeetingRoomAndBookingDate(filters.getMeetingRoom(), filters.getBookingDate(), pageable)
                .map(this::toMeetingDTO);
    }

    /**
     * Closes all conducted meetings by setting their status to "COMPLETED".
     */
    @Override
    @Transactional
    public void closeConductedMeetings() {
        List<Meeting> pastMeetings = meetingRepository.findAllByBookingDateBeforeAndStatus(LocalDate.now(), MeetingStatus.SCHEDULED);
        pastMeetings.forEach(booking -> booking.setStatus(MeetingStatus.COMPLETED));
    }

    private MeetingDTO toMeetingDTO(Meeting meeting) {
        return MeetingDTO.builder()
                .id(meeting.getId())
                .bookingDate(meeting.getBookingDate())
                .meetingRoom(meeting.getMeetingRoom())
                .bookedBy(meeting.getBookedBy())
                .startTime(meeting.getStartTime())
                .endTime(meeting.getEndTime())
                .build();
    }

    private void canCancel(Meeting meeting) {
        if (meeting.getBookingDate().isBefore(LocalDate.now())) {
            throw new BookingException(ErrorMessages.ARB_002_CANNOT_CANCEL_PAST_BOOKING);
        }
    }

    private void hasConflictingBooking(MeetingDTO dto) {
        meetingRepository.findBookingByBookedByAndBookingDateAndStartTimeAfterAndEndTimeBefore(
                dto.getBookedBy(), dto.getBookingDate(), dto.getStartTime(), dto.getEndTime()
        ).ifPresent(
                value -> {
                    throw new BookingException(ErrorMessages.ARB_007_HAS_A_CONFLICTING_BOOKING);
                }
        );
    }

    private void isDurationValid(MeetingDTO dto) {
        Duration duration = Duration.between(dto.getStartTime(), dto.getEndTime());

        if (duration.toMinutes() < 60 || duration.toMinutes() % 60 != 0) {
            throw new BookingException(ErrorMessages.ARB_005_MEETING_DURATION_IS_NOT_VALID);
        }
    }

    private void isTimeRounded(MeetingDTO dto) {
        boolean startingTime = dto.getStartTime().getMinute() == 0 | dto.getStartTime().getMinute() == 30;
        boolean endingTime = dto.getEndTime().getMinute() == 0 | dto.getEndTime().getMinute() == 30;

        if (!(startingTime && endingTime)) {
            throw new BookingException(ErrorMessages.ARB_004_MEETING_TIME_NOT_ROUNDED);
        }
    }

    private void hasOverlap(MeetingDTO dto) {
        List<Meeting> existingMeetings = meetingRepository.findAllByMeetingRoomAndBookingDateAndStartTimeBetween(
                dto.getMeetingRoom(),
                dto.getBookingDate(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        if (!existingMeetings.isEmpty()) {
            throw new BookingException(ErrorMessages.ARB_003_BOOKING_OVERLAP);
        }
    }

}
