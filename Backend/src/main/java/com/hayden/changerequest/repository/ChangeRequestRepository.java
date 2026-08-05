package com.hayden.changerequest.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.hayden.changerequest.entity.ChangeRequest;
import com.hayden.changerequest.common.enums.ChangeRequestStatus;

public interface ChangeRequestRepository extends JpaRepository<ChangeRequest, Long> {
    List<ChangeRequest> findBySubmittedBy_EmailOrderByCreatedAtDesc(
        String email

);
    List<ChangeRequest> findBySubmittedBy_EmailAndStatusOrderByCreatedAtDesc(
        String email,
        ChangeRequestStatus status
);
List<ChangeRequest> findByStatusNotOrderByCreatedAtDesc(ChangeRequestStatus status);
}
