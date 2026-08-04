package com.hayden.changerequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.hayden.changerequest.entity.ChangeRequest;

public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, Long> {
    List<ChangeRequest> findBySubmittedBy_EmailOrderByCreatedAtDesc(
        String email
);
}
