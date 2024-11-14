package com.acme.roombooker.service;

import com.acme.roombooker.model.AcmeUser;
import com.acme.roombooker.model.Meeting;
import com.acme.roombooker.enums.MeetingStatus;
import com.acme.roombooker.repository.MeetingRepository;
import com.acme.roombooker.dto.MeetingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.acme.roombooker.exception.EntityNotFoundException;
import com.acme.roombooker.exception.ErrorMessages;
import com.acme.roombooker.security.AcmePrincipal;
import com.acme.roombooker.utils.mappers.MeetingMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class MeetingServiceImpl implements MeetingService {

    private final MeetingRepository meetingRepository;
    private final AcmeUserService acmeUserService;
    private final MeetingMapper mapper;
    private final ValidationService validationService;


    public MeetingServiceImpl(MeetingRepository meetingRepository,
                              AcmeUserService acmeUserService,
                              MeetingMapper mapper,
                              ValidationService validationService) {
        this.meetingRepository = meetingRepository;
        this.acmeUserService = acmeUserService;
        this.mapper = mapper;
        this.validationService = validationService;
    }

    /**
     * Takes a Spring Pageable object and returns the bookings in a paginated form.
     * @param pageable The Pageable object containing pagination information.
     * @return A Page of BookingDTO objects.
     */
    @Override
    public Page<MeetingDTO> getAllMeetings(Pageable pageable) {
        return meetingRepository.findAll(pageable)
                .map(mapper::toMeetingDTO);
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
        validationService.isTimeRounded(dto);
        validationService.isDurationValid(dto);
        validationService.hasOverlap(dto);
        validationService.hasConflictingBooking(dto);

        AcmeUser acmeUser = acmeUserService.findAcmeUserByUsername(acmePrincipal.getUsername());

        Meeting meeting = mapper.toMeetingEntity(dto, acmeUser);

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
        validationService.canCancel(meeting);

        meeting.setStatus(MeetingStatus.CANCELLED);

        return mapper.toMeetingDTO(meeting);
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
                .map(mapper::toMeetingDTO);
    }

    /**
     * Closes all conducted meetings by setting their status to "COMPLETED".
     */
    @Override
    @Transactional
    public void closeConductedMeetings() {
        List<Meeting> pastMeetings = meetingRepository.findAllByBookingDateBeforeAndStatus(LocalDate.now(), MeetingStatus.SCHEDULED);
        pastMeetings.forEach(meeting -> meeting.setStatus(MeetingStatus.COMPLETED));
    }

}
