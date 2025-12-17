package com.hcmute.fit.toeicrise.controllers.admin;

import com.hcmute.fit.toeicrise.services.interfaces.IStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/admin/stats")
@RequiredArgsConstructor
public class DashboardController {
    private final IStatisticService statisticService;

    @GetMapping("/analytics")
    public ResponseEntity<?> analytics(@RequestParam LocalDate from, @RequestParam LocalDate to) {
        return ResponseEntity.ok(statisticService.getPerformanceAnalysis(from, to));
    }
}
