package com.testtask.doctorvisittracking.repository;

import com.testtask.doctorvisittracking.entity.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DoctorRepository extends JpaRepository<Doctor, Long> {
}
