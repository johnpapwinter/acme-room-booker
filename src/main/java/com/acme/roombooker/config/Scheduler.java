package com.acme.roombooker.config;

import com.acme.roombooker.service.BookingService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class Scheduler {

    private final BookingService bookingService;

    public Scheduler(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    // at 01:00 every day
    @Scheduled(cron = "0 0 1 * * ?")
    public void closeAllConductedMeetings() {
        bookingService.closeConductedMeetings();
    }

}
