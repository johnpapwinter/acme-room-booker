package com.acme.roombooker.service;

import com.acme.roombooker.model.Meeting;
import com.acme.roombooker.repository.MeetingRepository;
import com.acme.roombooker.dto.MeetingDTO;
import com.acme.roombooker.exception.BookingException;
import com.acme.roombooker.exception.ErrorMessages;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

@Service
public class ValidationServiceImpl implements ValidationService {

    private final MeetingRepository meetingRepository;

    public ValidationServiceImpl(MeetingRepository meetingRepository) {
        this.meetingRepository = meetingRepository;
    }


    @Override
    public void canCancel(Meeting meeting) {
        if (meeting.getBookingDate().isBefore(LocalDate.now())) {
            throw new BookingException(ErrorMessages.ARB_002_CANNOT_CANCEL_PAST_BOOKING);
        }
    }

    @Override
    public void hasConflictingBooking(MeetingDTO dto) {
        meetingRepository.findBookingByBookedByAndBookingDateAndStartTimeAfterAndEndTimeBefore(
                dto.getBookedBy(), dto.getBookingDate(), dto.getStartTime(), dto.getEndTime()
        ).ifPresent(
                value -> {
                    throw new BookingException(ErrorMessages.ARB_007_HAS_A_CONFLICTING_BOOKING);
                }
        );
    }

    @Override
    public void isDurationValid(MeetingDTO dto) {
        Duration duration = Duration.between(dto.getStartTime(), dto.getEndTime());

        if (duration.toMinutes() < 60 || duration.toMinutes() % 60 != 0) {
            throw new BookingException(ErrorMessages.ARB_005_MEETING_DURATION_IS_NOT_VALID);
        }
    }

    @Override
    public void isTimeRounded(MeetingDTO dto) {
        boolean startingTime = dto.getStartTime().getMinute() == 0 | dto.getStartTime().getMinute() == 30;
        boolean endingTime = dto.getEndTime().getMinute() == 0 | dto.getEndTime().getMinute() == 30;

        if (!(startingTime && endingTime)) {
            throw new BookingException(ErrorMessages.ARB_004_MEETING_TIME_NOT_ROUNDED);
        }
    }

    @Override
    public void hasOverlap(MeetingDTO dto) {
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
