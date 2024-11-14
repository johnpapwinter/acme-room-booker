package com.acme.roombooker.model;

import com.acme.roombooker.enums.MeetingRoom;
import com.acme.roombooker.enums.MeetingStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;


@Entity
@Table(name = "meetings")
@Getter
@Setter
public class Meeting implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "meeting_room", nullable = false)
    private MeetingRoom meetingRoom;

    @Column(name = "booked_by", nullable = false)
    private String bookedBy;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private MeetingStatus status;

    @ManyToOne
    @JoinColumn(name = "acme_user_id")
    private AcmeUser acmeUser;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Meeting meeting = (Meeting) o;
        return Objects.equals(id, meeting.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

}
