package com.testtask.doctorvisittracking.service;

import com.testtask.doctorvisittracking.dto.*;
import com.testtask.doctorvisittracking.entity.Doctor;
import com.testtask.doctorvisittracking.entity.Patient;
import com.testtask.doctorvisittracking.entity.Visit;
import com.testtask.doctorvisittracking.exception.BadRequestException;
import com.testtask.doctorvisittracking.exception.ResourceNotFoundException;
import com.testtask.doctorvisittracking.repository.DoctorRepository;
import com.testtask.doctorvisittracking.repository.PatientRepository;
import com.testtask.doctorvisittracking.repository.VisitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.time.OffsetDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static org.springframework.data.domain.PageRequest.of;

@RequiredArgsConstructor
@Service
public class VisitService {

    private static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd HH:mm:ssXXX");

    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final VisitRepository visitRepository;

    @Transactional
    public VisitResponse createVisit(VisitRequest visitRequest) {
        Doctor doctor = doctorRepository.findById(visitRequest.getDoctorId())
                .orElseThrow(() -> new ResourceNotFoundException("Doctor not found"));
        Patient patient = patientRepository.findById(visitRequest.getPatientId())
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));
        ZoneId doctorZone = ZoneId.of(doctor.getTimeZone());
        LocalDateTime startLocalDateTimeDoctorZone = convertToDoctorZone(visitRequest.getStartDateTime(), doctorZone);
        LocalDateTime endLocalDateTimeDoctorZone = convertToDoctorZone(visitRequest.getEndDateTime(), doctorZone);
        Optional<Visit> existingVisit = visitRepository.findBookedVisit(startLocalDateTimeDoctorZone,
                endLocalDateTimeDoctorZone, doctor.getId());
        if (existingVisit.isPresent()) {
            throw new BadRequestException("Doctor is already booked for this time");
        }
        Visit visit = new Visit();
        visit.setStartDateTime(startLocalDateTimeDoctorZone);
        visit.setEndDateTime(endLocalDateTimeDoctorZone);
        visit.setDoctor(doctor);
        visit.setPatient(patient);
        visitRepository.save(visit);
        return new VisitResponse(patient.getFirstName(), patient.getLastName(), doctor.getFirstName(),
                doctor.getLastName(), startLocalDateTimeDoctorZone, endLocalDateTimeDoctorZone);
    }

    public Page<PatientResponse> getPatientsWithVisits(int page, int size, String search, List<Long> doctorIds) {
        Pageable pageable = of(page, size);
        Page<Patient> patients = getPatients(search, pageable);
        Map<Long, Long> doctorIdsToPatientsCount = visitRepository.findAllDoctors().stream()
                .collect(groupingBy(identity(), counting()));
        List<PatientDto> patientDtos = patients.getContent().stream()
                .map(patient -> assemblePatientDto(patient, doctorIds, doctorIdsToPatientsCount))
                .filter(patientDto -> !patientDto.visits().isEmpty())
                .toList();
        return new PageImpl<>(List.of(new PatientResponse(patientDtos)), pageable, patients.getTotalElements());
    }

    private LocalDateTime convertToDoctorZone(String dateTime, ZoneId doctorZone) {
        ZonedDateTime zonedDateTime = parse(dateTime, FORMATTER).toZonedDateTime();
        ZonedDateTime dateTimeInDoctorZone = zonedDateTime.withZoneSameInstant(doctorZone);
        return dateTimeInDoctorZone.toLocalDateTime();
    }

    private Page<Patient> getPatients(String search, Pageable pageable) {
        return !search.isEmpty()
                ? patientRepository.findByFirstNameIgnoreCase(search, pageable)
                : patientRepository.findAllPatients(pageable);
    }

    private PatientDto assemblePatientDto(Patient patient, List<Long> doctorIds, Map<Long, Long> doctorIdsToPatientsCount) {
        List<Visit> visits = patient.getVisits().stream()
                .filter(visit -> doctorIds.isEmpty() || doctorIds.contains(visit.getDoctor().getId()))
                .toList();
        List<VisitDto> visitDtos = visits.stream()
                .map(visit -> assembleVisitDto(visit, doctorIdsToPatientsCount.get(visit.getDoctor().getId())))
                .toList();
        return new PatientDto(patient.getFirstName(), patient.getLastName(), visitDtos);
    }

    private VisitDto assembleVisitDto(Visit visit, Long visitCount) {
        return new VisitDto(visit.getStartDateTime().toString(), visit.getEndDateTime().toString(),
                new DoctorDto(visit.getDoctor().getFirstName(), visit.getDoctor().getLastName(), visitCount));
    }

}
