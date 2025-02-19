package com.testtask.doctorvisittracking.dto;

import jakarta.validation.constraints.AssertTrue;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZonedDateTime.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

@Getter
@Setter
@AllArgsConstructor
public class VisitRequest {

    private String startDateTime;
    private String endDateTime;
    private Long patientId;
    private Long doctorId;

    @AssertTrue(message = "End time must be after start time")
    public boolean isTimeValid() {
        DateTimeFormatter formatter = ofPattern("yyyy-MM-dd HH:mm:ssXXX");
        ZonedDateTime start = parse(startDateTime, formatter);
        ZonedDateTime end = parse(endDateTime, formatter);
        return end.isAfter(start);
    }

}
