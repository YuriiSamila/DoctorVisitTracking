package com.testtask.doctorvisittracking.dto;

public record VisitDto(String startDateTime, String endDateTime, DoctorDto doctor) {
}
