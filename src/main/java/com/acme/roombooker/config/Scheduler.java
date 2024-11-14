package com.acme.roombooker.config;

import com.acme.roombooker.service.MeetingService;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class Scheduler {

    private final MeetingService meetingService;

    public Scheduler(MeetingService meetingService) {
        this.meetingService = meetingService;
    }

    // at 01:00 every day
    @Scheduled(cron = "0 0 1 * * ?")
    public void closeAllConductedMeetings() {

        meetingService.closeConductedMeetings();
    }

}
