package com.testtask.doctorvisittracking.controller;

import com.testtask.doctorvisittracking.dto.PatientResponse;
import com.testtask.doctorvisittracking.dto.VisitRequest;
import com.testtask.doctorvisittracking.dto.VisitResponse;
import com.testtask.doctorvisittracking.service.VisitService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/visits")
public class VisitController {

    private final VisitService visitService;

    @PostMapping
    public VisitResponse createVisit(@RequestBody @Valid VisitRequest visitRequest) {
        return visitService.createVisit(visitRequest);
    }

    @GetMapping("/patients")
    public Page<PatientResponse> getPatients(@RequestParam int page, @RequestParam int size,
                                             @RequestParam String search, @RequestParam List<Long> doctorIds) {
        return visitService.getPatientsWithVisits(page, size, search, doctorIds);
    }

}
