package com.acme.roombooker.service;

import com.acme.roombooker.model.AcmeUser;
import com.acme.roombooker.model.Meeting;
import com.acme.roombooker.enums.AcmeRole;
import com.acme.roombooker.enums.MeetingRoom;
import com.acme.roombooker.enums.MeetingStatus;
import com.acme.roombooker.repository.MeetingRepository;
import com.acme.roombooker.dto.MeetingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.acme.roombooker.exception.BookingException;
import com.acme.roombooker.exception.EntityNotFoundException;
import com.acme.roombooker.exception.ErrorMessages;
import com.acme.roombooker.security.AcmePrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeetingServiceImplTest {

    @Mock
    private MeetingRepository meetingRepository;

    @Mock
    private AcmeUserService acmeUserService;

    @InjectMocks
    private MeetingServiceImpl bookingService;

    private MeetingDTO validMeetingDTO;
    private Meeting validMeeting1;
    private Meeting validMeeting2;
    private AcmeUser acmeUser;
    private AcmePrincipal acmePrincipal;

    @BeforeEach
    void setUp() {
        validMeetingDTO = MeetingDTO.builder()
                .meetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM)
                .bookedBy("elmerfudd@acme.com")
                .bookingDate(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .build();

        validMeeting1 = new Meeting();
        validMeeting1.setId(1L);
        validMeeting1.setMeetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM);
        validMeeting1.setBookedBy("yoshemitesam@acme.com");
        validMeeting1.setBookingDate(LocalDate.now());
        validMeeting1.setStartTime(LocalTime.of(10, 0, 1));
        validMeeting1.setEndTime(LocalTime.of(11, 0));
        validMeeting1.setStatus(MeetingStatus.SCHEDULED);

        validMeeting2 = new Meeting();
        validMeeting2.setId(2L);
        validMeeting2.setMeetingRoom(MeetingRoom.ELMER_FUDD_ROOM);
        validMeeting2.setBookedBy("wilecoyote@acme.com");
        validMeeting2.setBookingDate(LocalDate.now());
        validMeeting2.setStartTime(LocalTime.of(9, 30, 1));
        validMeeting2.setEndTime(LocalTime.of(10, 30));
        validMeeting2.setStatus(MeetingStatus.SCHEDULED);

        acmeUser = new AcmeUser();
        acmeUser.setId(1L);
        acmeUser.setEmail("elmerfudd@acme.com");
        acmeUser.setUsername("elmer");
        acmeUser.setRole(AcmeRole.ADMIN);

        acmePrincipal = AcmePrincipal.builder()
                .id(1L)
                .username("elmer")
                .email("elmerfudd@acme.com")
                .build();
    }

    @Test
    @Disabled
    @DisplayName("Should return a pageable with bookings")
    void shouldGetAllMeetings() {
        // given
        Pageable pageable = Pageable.unpaged();
        Page<Meeting> bookingPage = new PageImpl<>(Arrays.asList(validMeeting1, validMeeting2));
        when(meetingRepository.findAll(pageable)).thenReturn(bookingPage);

        // when
        Page<MeetingDTO> result = bookingService.getAllMeetings(pageable);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(meetingRepository).findAll(pageable);
    }

    @Test
    @Disabled
    @DisplayName("Should successfully add a new booking")
    void shouldAddMeeting() {
        // given
        when(meetingRepository.findAllByMeetingRoomAndBookingDateAndStartTimeBetween(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(acmeUserService.findAcmeUserByUsername(any())).thenReturn(acmeUser);
        ArgumentCaptor<Meeting> bookingCaptor = ArgumentCaptor.forClass(Meeting.class);

        // when
        bookingService.addMeeting(validMeetingDTO, acmePrincipal);

        // then
        verify(meetingRepository).save(bookingCaptor.capture());
        Meeting result = bookingCaptor.getValue();

        assertNotNull(result);
        assertEquals(validMeetingDTO.getId(), result.getId());
        assertEquals(validMeetingDTO.getMeetingRoom(), result.getMeetingRoom());
        assertEquals(validMeetingDTO.getBookingDate(), result.getBookingDate());
        assertEquals(validMeetingDTO.getStartTime().plusSeconds(1), result.getStartTime());
        assertEquals(validMeetingDTO.getEndTime(), result.getEndTime());
        assertEquals(MeetingStatus.SCHEDULED, result.getStatus());
    }

    @Test
    @Disabled
    @DisplayName("Should throw invalid duration error")
    void shouldThrowExceptionForDuration() {
        // given
        MeetingDTO dto = MeetingDTO.builder()
                .meetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM)
                .bookedBy("yoshemitesam@acme.com")
                .bookingDate(LocalDate.now())
                .startTime(LocalTime.of(10, 0))
                .endTime(LocalTime.of(11, 0))
                .build();

        // when
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.addMeeting(dto, acmePrincipal));

        // then
        assertEquals(ErrorMessages.ARB_005_MEETING_DURATION_IS_NOT_VALID, exception.getMessage());
    }

    @Test
    @Disabled
    @DisplayName("Should throw invalid time error")
    void shouldThrowExceptionForTime() {
        // given
        MeetingDTO dto = MeetingDTO.builder()
                .meetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM)
                .bookedBy("yoshemitesam@acme.com")
                .bookingDate(LocalDate.now())
                .startTime(LocalTime.of(10, 5))
                .endTime(LocalTime.of(11, 0))
                .build();

        // when
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.addMeeting(dto, acmePrincipal));

        // then
        assertEquals(ErrorMessages.ARB_004_MEETING_TIME_NOT_ROUNDED, exception.getMessage());
    }

    @Test
    @Disabled
    @DisplayName("Should throw meeting overlap error")
    void shouldThrowExceptionForOverlap() {
        // given
        when(meetingRepository.findAllByMeetingRoomAndBookingDateAndStartTimeBetween(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(validMeeting1));

        // when
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.addMeeting(validMeetingDTO, acmePrincipal));

        // then
        assertEquals(ErrorMessages.ARB_003_BOOKING_OVERLAP, exception.getMessage());
    }

    @Test
    @Disabled
    @DisplayName("Should successfully cancel an existing booking")
    void shouldCancelMeeting() {
        // given
        when(meetingRepository.findById(1L)).thenReturn(Optional.of(validMeeting1));

        // when
        bookingService.cancelMeeting(1L);

        // then
        assertEquals(MeetingStatus.CANCELLED, validMeeting1.getStatus());
        verify(meetingRepository).findById(1L);
    }

    @Test
    @Disabled
    @DisplayName("Should throw an error when cancelling non-existing booking")
    void shouldThrowNotFoundWhenCancellingNonExistingBooking() {
        // given
        when(meetingRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> bookingService.cancelMeeting(1L));

        // then
        assertEquals(ErrorMessages.ARB_001_BOOKING_NOT_FOUND, exception.getMessage());
    }

    @Test
    @Disabled
    @DisplayName("Should throw an error when cancelling past booking")
    void shouldNotCancelPastBooking() {
        // given
        Meeting pastMeeting = new Meeting();
        pastMeeting.setId(1L);
        pastMeeting.setBookingDate(LocalDate.now().minusDays(1));
        pastMeeting.setStatus(MeetingStatus.SCHEDULED);

        when(meetingRepository.findById(1L)).thenReturn(Optional.of(pastMeeting));

        // when
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.cancelMeeting(1L));

        // then
        assertEquals(ErrorMessages.ARB_002_CANNOT_CANCEL_PAST_BOOKING, exception.getMessage());
    }


    @Test
    @Disabled
    @DisplayName("Should successfully search existing bookings")
    void shouldSearchBookings() {
        // given
        SearchFiltersDTO filtersDTO = new SearchFiltersDTO();
        filtersDTO.setMeetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM);
        filtersDTO.setBookingDate(LocalDate.now());
        Pageable pageable = Pageable.unpaged();

        Page<Meeting> bookingPage = new PageImpl<>(Collections.singletonList(validMeeting1));
        when(meetingRepository.findAllByMeetingRoomAndBookingDate(MeetingRoom.MAIN_CONFERENCE_ROOM, LocalDate.now(), pageable))
                .thenReturn(bookingPage);

        // when
        Page<MeetingDTO> result = bookingService.search(filtersDTO, pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(meetingRepository).findAllByMeetingRoomAndBookingDate(MeetingRoom.MAIN_CONFERENCE_ROOM, LocalDate.now(), pageable);
    }

    @Test
    @Disabled
    @DisplayName("Should complete past bookings")
    void shouldCompletePastBookings() {
        // given
        when(meetingRepository.findAllByBookingDateBeforeAndStatus(LocalDate.now(), MeetingStatus.SCHEDULED))
                .thenReturn(Arrays.asList(validMeeting1, validMeeting2));

        // when
        bookingService.closeConductedMeetings();

        // then
        assertEquals(MeetingStatus.COMPLETED, validMeeting1.getStatus());
        assertEquals(MeetingStatus.COMPLETED, validMeeting2.getStatus());

    }

}