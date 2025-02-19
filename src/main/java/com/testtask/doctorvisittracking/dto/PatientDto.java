package com.testtask.doctorvisittracking.dto;

import java.util.List;

public record PatientDto(String patientFirstName, String patientLastName, List<VisitDto> visits) {
}
