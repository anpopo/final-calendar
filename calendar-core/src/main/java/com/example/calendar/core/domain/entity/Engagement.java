package com.example.calendar.core.domain.entity;

import com.example.calendar.core.domain.Event;
import com.example.calendar.core.domain.RequestReplyType;
import com.example.calendar.core.domain.RequestStatus;
import com.example.calendar.core.util.Period;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Table(name = "engagements")
@Entity
public class Engagement extends BaseEntity {

    @JoinColumn(name = "schedule_id")
    @ManyToOne
    private Schedule schedule;

    @JoinColumn(name = "attendee_id")
    @ManyToOne
    private User attendee;

    @Enumerated(EnumType.STRING)
    private RequestStatus requestStatus;

    @Builder
    public Engagement(Schedule schedule, User attendee, RequestStatus requestStatus) {
        this.schedule = schedule;
        this.attendee = attendee;
        this.requestStatus = requestStatus;
    }

    public Event getEvent() {
        return this.schedule.toEvent();
    }

    public boolean isOverlapped(Period period) {
        return this.schedule.isOverlapped(period);
    }

    public void updateRequestStatus(RequestReplyType type) {
        switch (type) {
            case ACCEPT:
                this.requestStatus = RequestStatus.ACCEPTED;
                break;
            case REJECT:
                this.requestStatus = RequestStatus.REJECTED;
                break;
            default:
                throw new IllegalArgumentException("Invalid reply type");
        }
    }
}
