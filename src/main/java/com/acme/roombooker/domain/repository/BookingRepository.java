package com.acme.roombooker.domain.repository;

import com.acme.roombooker.domain.entity.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;


@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findAllByRoomAndBookingDate(String room, LocalDate bookingDate, Pageable pageable);

}
