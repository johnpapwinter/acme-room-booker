package com.acme.roombooker.domain.repository;

import com.acme.roombooker.domain.entity.Booking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByRoomAndBookingDate(String room, LocalDate bookingDate);

}
