package com.hayden.changerequest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hayden.changerequest.entity.ChangeRequestStatusHistory;

public interface ChangeRequestStatusHistoryRepository
        extends JpaRepository<ChangeRequestStatusHistory, Long> {

    List<ChangeRequestStatusHistory>
            findByChangeRequest_IdOrderByChangedAtAsc(Long changeRequestId);
}