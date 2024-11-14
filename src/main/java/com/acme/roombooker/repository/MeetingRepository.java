package com.acme.roombooker.repository;

import com.acme.roombooker.model.Meeting;
import com.acme.roombooker.enums.MeetingStatus;
import com.acme.roombooker.enums.MeetingRoom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface MeetingRepository extends JpaRepository<Meeting, Long> {

    Page<Meeting> findAllByMeetingRoomAndBookingDate(MeetingRoom meetingRoom, LocalDate bookingDate, Pageable pageable);

    List<Meeting> findAllByMeetingRoomAndBookingDateAndStartTimeBetween(MeetingRoom meetingRoom, LocalDate date, LocalTime start, LocalTime end);

    List<Meeting> findAllByBookingDateBeforeAndStatus(LocalDate bookingDate, MeetingStatus status);

    Optional<Meeting> findBookingByBookedByAndBookingDateAndStartTimeAfterAndEndTimeBefore(String bookedBy,
                                                                                           LocalDate bookingDate,
                                                                                           LocalTime start,
                                                                                           LocalTime end);

}
