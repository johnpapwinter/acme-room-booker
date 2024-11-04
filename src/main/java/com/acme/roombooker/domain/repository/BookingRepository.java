package com.acme.roombooker.domain.repository;

import com.acme.roombooker.domain.entity.Booking;
import com.acme.roombooker.domain.enums.MeetingStatus;
import com.acme.roombooker.domain.enums.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByRoomAndBookingDate(Room room, LocalDate bookingDate, Pageable pageable);

    List<Booking> findAllByRoomAndBookingDateAndStartTimeBetween(Room room, LocalDate date, LocalTime start, LocalTime end);

    List<Booking> findAllByBookingDateBeforeAndStatus(LocalDate bookingDate, MeetingStatus status);

    Optional<Booking> findBookingByBookedByAndBookingDateAndStartTimeAfterAndEndTimeBefore(String bookedBy,
                                                                                           LocalDate bookingDate,
                                                                                           LocalTime start,
                                                                                           LocalTime end);

}
