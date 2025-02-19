package com.testtask.doctorvisittracking.service;

import com.testtask.doctorvisittracking.dto.PatientDto;
import com.testtask.doctorvisittracking.dto.PatientResponse;
import com.testtask.doctorvisittracking.dto.VisitRequest;
import com.testtask.doctorvisittracking.dto.VisitResponse;
import com.testtask.doctorvisittracking.entity.Doctor;
import com.testtask.doctorvisittracking.entity.Patient;
import com.testtask.doctorvisittracking.entity.Visit;
import com.testtask.doctorvisittracking.exception.BadRequestException;
import com.testtask.doctorvisittracking.exception.ResourceNotFoundException;
import com.testtask.doctorvisittracking.repository.DoctorRepository;
import com.testtask.doctorvisittracking.repository.PatientRepository;
import com.testtask.doctorvisittracking.repository.VisitRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static java.time.LocalDateTime.of;
import static java.util.Optional.of;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VisitServiceTest {

    @Mock
    private VisitRepository visitRepository;

    @Mock
    private DoctorRepository doctorRepository;

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private VisitService visitService;

    @Test
    void createVisitTest() {
        VisitRequest visitRequest = createRequest();
        Doctor doctor = createDoctor();
        Patient patient = createPatient(1L, "Jane", "Marry");
        when(doctorRepository.findById(2L)).thenReturn(of(doctor));
        when(patientRepository.findById(1L)).thenReturn(of(patient));
        VisitResponse response = visitService.createVisit(visitRequest);
        assertNotNull(response);
        assertEquals("Marry", response.patientLastName());
        assertEquals("Torry", response.doctorLastName());
        verify(visitRepository).findBookedVisit(any(LocalDateTime.class), any(LocalDateTime.class), any(Long.class));
        verify(visitRepository).save(any(Visit.class));
    }

    @Test
    void createVisitWithBadRequestExceptionTest() {
        VisitRequest visitRequest = createRequest();
        Doctor doctor = createDoctor();
        Patient patient = createPatient(1L, "Jane", "Marry");
        when(doctorRepository.findById(2L)).thenReturn(of(doctor));
        when(patientRepository.findById(1L)).thenReturn(of(patient));
        when(visitRepository.findBookedVisit(any(LocalDateTime.class), any(LocalDateTime.class), any(Long.class)))
                .thenReturn(Optional.of(new Visit()));
        BadRequestException exception = assertThrowsExactly(BadRequestException.class,
                () -> visitService.createVisit(visitRequest));
        assertEquals("Doctor is already booked for this time", exception.getMessage());
    }

    @Test
    void createVisitWithDoctorNotFoundExceptionTest() {
        ResourceNotFoundException exception = assertThrowsExactly(ResourceNotFoundException.class,
                () -> visitService.createVisit(createRequest()));
        assertEquals("Doctor not found", exception.getMessage());
    }

    @Test
    void createVisitWithPatientNotFoundExceptionTest() {
        when(doctorRepository.findById(2L)).thenReturn(of(createDoctor()));
        ResourceNotFoundException exception = assertThrowsExactly(ResourceNotFoundException.class,
                () -> visitService.createVisit(createRequest()));
        assertEquals("Patient not found", exception.getMessage());
    }

    @Test
    void getAllPatientsWithVisitsTest() {
        Pageable pageable = PageRequest.of(0, 2);
        when(patientRepository.findAllPatients(pageable)).thenReturn(new PageImpl<>(createPatientsWithVisits()));
        List<Long> doctorIds = List.of(2L);
        when(visitRepository.findAllDoctors()).thenReturn(doctorIds);
        Page<PatientResponse> responsePage = visitService.getPatientsWithVisits(0, 2, "", doctorIds);
        assertEquals(2, responsePage.getTotalElements());
        assertEquals(1, responsePage.getTotalPages());
        PatientDto firstPatientDto = responsePage.getContent().getFirst().getPatientDtos().getLast();
        assertEquals("Sam", firstPatientDto.patientFirstName());
        assertEquals("Elliot", firstPatientDto.patientLastName());

    }

    @Test
    void getPatientsWithVisitsTest() {
        Pageable pageable = PageRequest.of(0, 2);
        when(patientRepository.findByFirstNameIgnoreCase("Jane", pageable))
                .thenReturn(new PageImpl<>(List.of(createPatientsWithVisits().getFirst())));
        List<Long> doctorIds = List.of(2L);
        when(visitRepository.findAllDoctors()).thenReturn(doctorIds);
        Page<PatientResponse> responsePage = visitService.getPatientsWithVisits(0, 2, "Jane", doctorIds);
        assertEquals(1, responsePage.getTotalElements());
        assertEquals(1, responsePage.getTotalPages());
        PatientDto lastPatientDto = responsePage.getContent().getFirst().getPatientDtos().getFirst();
        assertEquals("Jane", lastPatientDto.patientFirstName());
        assertEquals("Marry", lastPatientDto.patientLastName());
    }

    @Test
    void getPatientsWithVisitsForAllDoctorsTest() {
        Pageable pageable = PageRequest.of(0, 2);
        when(patientRepository.findByFirstNameIgnoreCase("Jane", pageable))
                .thenReturn(new PageImpl<>(createPatientsWithVisits()));
        List<Long> doctorIds = List.of(2L);
        when(visitRepository.findAllDoctors()).thenReturn(doctorIds);
        Page<PatientResponse> responsePage = visitService.getPatientsWithVisits(0, 2, "Jane", List.of());
        assertEquals(2, responsePage.getTotalElements());
        assertEquals(1, responsePage.getTotalPages());
        PatientDto lastPatientDto = responsePage.getContent().getFirst().getPatientDtos().getFirst();
        assertEquals("Jane", lastPatientDto.patientFirstName());
        assertEquals("Marry", lastPatientDto.patientLastName());
    }

    private static VisitRequest createRequest() {
        return new VisitRequest("2025-02-20 10:00:00+02:00",
                "2025-02-20 10:30:00+02:00", 1L, 2L);
    }

    private static Doctor createDoctor() {
        Doctor doctor = new Doctor();
        doctor.setId(2L);
        doctor.setFirstName("John");
        doctor.setLastName("Torry");
        doctor.setTimeZone("UTC");
        return doctor;
    }

    private static Patient createPatient(long id, String firstName, String lastName) {
        Patient patient = new Patient();
        patient.setId(id);
        patient.setFirstName(firstName);
        patient.setLastName(lastName);
        return patient;
    }

    private static List<Patient> createPatientsWithVisits() {
        Patient patient1 = createPatient(1L, "Jane", "Marry");
        Visit visit1 = createVisit(of(2025, 2, 20, 10, 0, 0),
                of(2025, 2, 20, 10, 30, 0));
        patient1.setVisits(List.of(visit1));
        Patient patient2 = createPatient(2L, "Sam", "Elliot");
        Visit visit2 = createVisit(of(2025, 3, 10, 10, 0, 0),
                of(2025, 3, 10, 10, 30, 0));
        patient2.setVisits(List.of(visit2));
        return List.of(patient1, patient2);
    }

    private static Visit createVisit(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        Visit visit = new Visit();
        visit.setDoctor(createDoctor());
        visit.setStartDateTime(startDateTime);
        visit.setEndDateTime(endDateTime);
        visit.setStartDateTime(of(2025, 2, 20, 10, 0, 0));
        visit.setEndDateTime(of(2025, 2, 20, 10, 30, 0));
        return visit;
    }

}