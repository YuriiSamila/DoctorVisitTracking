package com.testtask.doctorvisittracking.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PatientResponse {

    private List<PatientDto> patientDtos;
    private int count;

    public PatientResponse(List<PatientDto> patientDtos) {
        this.patientDtos = patientDtos;
        count = patientDtos.size();
    }
}
