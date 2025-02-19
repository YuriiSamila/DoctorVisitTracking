package com.testtask.doctorvisittracking.repository;

import com.testtask.doctorvisittracking.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Query("""
            SELECT v FROM Visit v WHERE v.doctor.id = :doctorId
                        AND (:startDateTime <= v.endDateTime AND :endDateTime >= v.startDateTime)""")
    Optional<Visit> findBookedVisit(LocalDateTime startDateTime, LocalDateTime endDateTime, Long doctorId);

    @Query("SELECT v.doctor.id FROM Visit v GROUP BY v.doctor.id, v.patient.id")
    List<Long> findAllDoctors();

}
