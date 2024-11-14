package com.acme.roombooker.utils.mappers;

import com.acme.roombooker.model.AcmeUser;
import com.acme.roombooker.model.Meeting;
import com.acme.roombooker.enums.MeetingStatus;
import com.acme.roombooker.dto.MeetingDTO;
import org.springframework.stereotype.Component;

@Component
public class MeetingMapper {

    public MeetingDTO toMeetingDTO(Meeting meeting) {
        return MeetingDTO.builder()
                .id(meeting.getId())
                .bookingDate(meeting.getBookingDate())
                .meetingRoom(meeting.getMeetingRoom())
                .bookedBy(meeting.getBookedBy())
                .startTime(meeting.getStartTime())
                .endTime(meeting.getEndTime())
                .build();
    }


    public Meeting toMeetingEntity(MeetingDTO dto, AcmeUser acmeUser) {
        Meeting meeting = new Meeting();
        meeting.setMeetingRoom(dto.getMeetingRoom());
        meeting.setBookedBy(acmeUser.getEmail());
        meeting.setBookingDate(dto.getBookingDate());
        // since bookings are always on the top of the hour or half-hours and in order to avoid
        // overlap errors, we add 1 second to the start time, it could be nanos, but seconds will do in this case
        meeting.setStartTime(dto.getStartTime().plusSeconds(1));
        meeting.setEndTime(dto.getEndTime());
        meeting.setStatus(MeetingStatus.SCHEDULED);
        meeting.setAcmeUser(acmeUser);
        acmeUser.getMeetings().add(meeting);

        return meeting;
    }

}
