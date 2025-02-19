package com.testtask.doctorvisittracking.dto;

import java.time.LocalDateTime;

public record VisitResponse(String patientFirstName, String patientLastName, String doctorFirstName,
                            String doctorLastName, LocalDateTime visitStartDateTime, LocalDateTime visitEndDateTime) {

}
