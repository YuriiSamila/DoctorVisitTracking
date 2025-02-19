package com.testtask.doctorvisittracking.repository;

import com.testtask.doctorvisittracking.entity.Patient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PatientRepository extends JpaRepository<Patient, Long> {

    @Query("""
            SELECT DISTINCT p FROM Patient p LEFT JOIN FETCH p.visits v LEFT JOIN FETCH v.doctor
                        WHERE LOWER(p.lastName) = LOWER(:search)""")
    Page<Patient> findByFirstNameIgnoreCase(String search, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Patient p LEFT JOIN FETCH p.visits v LEFT JOIN FETCH v.doctor")
    Page<Patient> findAllPatients(Pageable pageable);

}
