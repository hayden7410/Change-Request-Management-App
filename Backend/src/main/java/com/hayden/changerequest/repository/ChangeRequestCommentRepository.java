package com.hayden.changerequest.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hayden.changerequest.entity.ChangeRequestComment;

public interface ChangeRequestCommentRepository
        extends JpaRepository<ChangeRequestComment, Long> {

    List<ChangeRequestComment>
            findByChangeRequest_IdOrderByCreatedAtAsc(Long changeRequestId);
}