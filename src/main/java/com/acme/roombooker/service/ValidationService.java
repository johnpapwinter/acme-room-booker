package com.acme.roombooker.service;

import com.acme.roombooker.model.Meeting;
import com.acme.roombooker.dto.MeetingDTO;

public interface ValidationService {

    void canCancel(Meeting meeting);

    void hasConflictingBooking(MeetingDTO dto);

    void isDurationValid(MeetingDTO dto);

    void isTimeRounded(MeetingDTO dto);

    void hasOverlap(MeetingDTO dto);

}
