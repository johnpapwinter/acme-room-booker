package com.acme.roombooker.service;

import com.acme.roombooker.domain.entity.AcmeUser;
import com.acme.roombooker.domain.entity.Booking;
import com.acme.roombooker.domain.enums.AcmeRole;
import com.acme.roombooker.domain.enums.MeetingRoom;
import com.acme.roombooker.domain.enums.MeetingStatus;
import com.acme.roombooker.domain.repository.BookingRepository;
import com.acme.roombooker.dto.BookingDTO;
import com.acme.roombooker.dto.SearchFiltersDTO;
import com.acme.roombooker.exception.BookingException;
import com.acme.roombooker.exception.EntityNotFoundException;
import com.acme.roombooker.exception.ErrorMessages;
import com.acme.roombooker.security.AcmePrincipal;
import org.junit.jupiter.api.BeforeEach;
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
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private AcmeUserService acmeUserService;

    @InjectMocks
    private BookingServiceImpl bookingService;

    private BookingDTO validBookingDTO;
    private Booking validBooking1;
    private Booking validBooking2;
    private AcmeUser acmeUser;
    private AcmePrincipal acmePrincipal;

    @BeforeEach
    void setUp() {
        validBookingDTO = new BookingDTO();
        validBookingDTO.setMeetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM);
        validBookingDTO.setBookedBy("elmerfudd@acme.com");
        validBookingDTO.setBookingDate(LocalDate.now());
        validBookingDTO.setStartTime(LocalTime.of(10, 0));
        validBookingDTO.setEndTime(LocalTime.of(11, 0));

        validBooking1 = new Booking();
        validBooking1.setId(1L);
        validBooking1.setMeetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM);
        validBooking1.setBookedBy("yoshemitesam@acme.com");
        validBooking1.setBookingDate(LocalDate.now());
        validBooking1.setStartTime(LocalTime.of(10, 0, 1));
        validBooking1.setEndTime(LocalTime.of(11, 0));
        validBooking1.setStatus(MeetingStatus.SCHEDULED);

        validBooking2 = new Booking();
        validBooking2.setId(2L);
        validBooking2.setMeetingRoom(MeetingRoom.ELMER_FUDD_ROOM);
        validBooking2.setBookedBy("wilecoyote@acme.com");
        validBooking2.setBookingDate(LocalDate.now());
        validBooking2.setStartTime(LocalTime.of(9, 30, 1));
        validBooking2.setEndTime(LocalTime.of(10, 30));
        validBooking2.setStatus(MeetingStatus.SCHEDULED);

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
    @DisplayName("Should return a pageable with bookings")
    void shouldGetAllBookings() {
        // given
        Pageable pageable = Pageable.unpaged();
        Page<Booking> bookingPage = new PageImpl<>(Arrays.asList(validBooking1, validBooking2));
        when(bookingRepository.findAll(pageable)).thenReturn(bookingPage);

        // when
        Page<BookingDTO> result = bookingService.getAllBookings(pageable);

        // then
        assertNotNull(result);
        assertEquals(2, result.getTotalElements());
        verify(bookingRepository).findAll(pageable);
    }

    @Test
    @DisplayName("Should successfully add a new booking")
    void shouldAddBooking() {
        // given
        when(bookingRepository.findAllByMeetingRoomAndBookingDateAndStartTimeBetween(any(), any(), any(), any()))
                .thenReturn(Collections.emptyList());
        when(acmeUserService.findAcmeUserByUsername(any())).thenReturn(acmeUser);
        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);

        // when
        bookingService.addBooking(validBookingDTO, acmePrincipal);

        // then
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking result = bookingCaptor.getValue();

        assertNotNull(result);
        assertEquals(validBookingDTO.getId(), result.getId());
        assertEquals(validBookingDTO.getMeetingRoom(), result.getMeetingRoom());
        assertEquals(validBookingDTO.getBookingDate(), result.getBookingDate());
        assertEquals(validBookingDTO.getStartTime().plusSeconds(1), result.getStartTime());
        assertEquals(validBookingDTO.getEndTime(), result.getEndTime());
        assertEquals(MeetingStatus.SCHEDULED, result.getStatus());
    }

    @Test
    @DisplayName("Should throw invalid duration error")
    void shouldThrowExceptionForDuration() {
        // given
        BookingDTO dto = new BookingDTO();
        dto.setMeetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM);
        dto.setBookedBy("yoshemitesam@acme.com");
        dto.setBookingDate(LocalDate.now());
        dto.setStartTime(LocalTime.of(10, 0));
        dto.setEndTime(LocalTime.of(11, 30));

        // when
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.addBooking(dto, acmePrincipal));

        // then
        assertEquals(ErrorMessages.ARB_005_MEETING_DURATION_IS_NOT_VALID.name(), exception.getMessage());
    }

    @Test
    @DisplayName("Should throw invalid time error")
    void shouldThrowExceptionForTime() {
        // given
        BookingDTO dto = new BookingDTO();
        dto.setMeetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM);
        dto.setBookedBy("yoshemitesam@acme.com");
        dto.setBookingDate(LocalDate.now());
        dto.setStartTime(LocalTime.of(10, 5));
        dto.setEndTime(LocalTime.of(11, 0));

        // when
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.addBooking(dto, acmePrincipal));

        // then
        assertEquals(ErrorMessages.ARB_004_MEETING_TIME_NOT_ROUNDED.name(), exception.getMessage());
    }

    @Test
    @DisplayName("Should throw meeting overlap error")
    void shouldThrowExceptionForOverlap() {
        // given
        when(bookingRepository.findAllByMeetingRoomAndBookingDateAndStartTimeBetween(any(), any(), any(), any()))
                .thenReturn(Collections.singletonList(validBooking1));

        // when
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.addBooking(validBookingDTO, acmePrincipal));

        // then
        assertEquals(ErrorMessages.ARB_003_BOOKING_OVERLAP.name(), exception.getMessage());
    }

    @Test
    @DisplayName("Should successfully cancel an existing booking")
    void shouldCancelBooking() {
        // given
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(validBooking1));

        // when
        bookingService.cancelBooking(1L);

        // then
        assertEquals(MeetingStatus.CANCELLED, validBooking1.getStatus());
        verify(bookingRepository).findById(1L);
    }

    @Test
    @DisplayName("Should throw an error when cancelling non-existing booking")
    void shouldThrowNotFoundWhenCancellingNonExistingBooking() {
        // given
        when(bookingRepository.findById(1L)).thenReturn(Optional.empty());

        // when
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> bookingService.cancelBooking(1L));

        // then
        assertEquals(ErrorMessages.ARB_001_BOOKING_NOT_FOUND.name(), exception.getMessage());
    }

    @Test
    @DisplayName("Should throw an error when cancelling past booking")
    void shouldNotCancelPastBooking() {
        // given
        Booking pastBooking = new Booking();
        pastBooking.setId(1L);
        pastBooking.setBookingDate(LocalDate.now().minusDays(1));
        pastBooking.setStatus(MeetingStatus.SCHEDULED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(pastBooking));

        // when
        BookingException exception = assertThrows(BookingException.class, () -> bookingService.cancelBooking(1L));

        // then
        assertEquals(ErrorMessages.ARB_002_CANNOT_CANCEL_PAST_BOOKING.name(), exception.getMessage());
    }


    @Test
    @DisplayName("Should successfully search existing bookings")
    void shouldSearchBookings() {
        // given
        SearchFiltersDTO filtersDTO = new SearchFiltersDTO();
        filtersDTO.setMeetingRoom(MeetingRoom.MAIN_CONFERENCE_ROOM);
        filtersDTO.setBookingDate(LocalDate.now());
        Pageable pageable = Pageable.unpaged();

        Page<Booking> bookingPage = new PageImpl<>(Collections.singletonList(validBooking1));
        when(bookingRepository.findAllByMeetingRoomAndBookingDate(MeetingRoom.MAIN_CONFERENCE_ROOM, LocalDate.now(), pageable))
                .thenReturn(bookingPage);

        // when
        Page<BookingDTO> result = bookingService.search(filtersDTO, pageable);

        // then
        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(bookingRepository).findAllByMeetingRoomAndBookingDate(MeetingRoom.MAIN_CONFERENCE_ROOM, LocalDate.now(), pageable);
    }

    @Test
    @DisplayName("Should complete past bookings")
    void shouldCompletePastBookings() {
        // given
        when(bookingRepository.findAllByBookingDateBeforeAndStatus(LocalDate.now(), MeetingStatus.SCHEDULED))
                .thenReturn(Arrays.asList(validBooking1, validBooking2));

        // when
        bookingService.closeConductedMeetings();

        // then
        assertEquals(MeetingStatus.COMPLETED, validBooking1.getStatus());
        assertEquals(MeetingStatus.COMPLETED, validBooking2.getStatus());

    }

}