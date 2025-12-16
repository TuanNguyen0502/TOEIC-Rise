package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.services.interfaces.IStatisticService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/staff/stats")
@RequiredArgsConstructor
public class StatisticController {
    private final IStatisticService statisticService;

    @GetMapping("/system-overview")
    public ResponseEntity<?> systemOverview() {
        return ResponseEntity.ok(statisticService.getSystemOverview());
    }
}
