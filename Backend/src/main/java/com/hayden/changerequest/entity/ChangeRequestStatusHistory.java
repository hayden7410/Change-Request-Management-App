package com.hayden.changerequest.entity;

import java.time.Instant;

import com.hayden.changerequest.common.enums.ChangeRequestStatus;

import jakarta.persistence.*;

@Entity
@Table(name = "change_request_status_history")
public class ChangeRequestStatusHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "change_request_id", nullable = false)
    private ChangeRequest changeRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "previous_status", nullable = false)
    private ChangeRequestStatus previousStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", nullable = false)
    private ChangeRequestStatus newStatus;

    @ManyToOne(optional = false)
    @JoinColumn(name = "changed_by_user_id", nullable = false)
    private User changedBy;

    @Column(name = "changed_at", nullable = false)
    private Instant changedAt;

    @PrePersist
    public void prePersist() {
        changedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public ChangeRequest getChangeRequest() {
        return changeRequest;
    }

    public void setChangeRequest(ChangeRequest changeRequest) {
        this.changeRequest = changeRequest;
    }

    public ChangeRequestStatus getPreviousStatus() {
        return previousStatus;
    }

    public void setPreviousStatus(ChangeRequestStatus previousStatus) {
        this.previousStatus = previousStatus;
    }

    public ChangeRequestStatus getNewStatus() {
        return newStatus;
    }

    public void setNewStatus(ChangeRequestStatus newStatus) {
        this.newStatus = newStatus;
    }

    public User getChangedBy() {
        return changedBy;
    }

    public void setChangedBy(User changedBy) {
        this.changedBy = changedBy;
    }

    public Instant getChangedAt() {
        return changedAt;
    }
}