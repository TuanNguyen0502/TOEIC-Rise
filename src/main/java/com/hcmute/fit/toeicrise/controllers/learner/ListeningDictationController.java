package com.hcmute.fit.toeicrise.controllers.learner;

import com.hcmute.fit.toeicrise.services.interfaces.IDictationTranscriptService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("learnerListeningDictationController")
@RequestMapping("/learner/dictation")
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class ListeningDictationController {
    IDictationTranscriptService dictationTranscriptService;

    @GetMapping("/library")
    public ResponseEntity<?> getDictationLibrary() {
        return ResponseEntity.ok(dictationTranscriptService.getDictationLibrary());
    }
}
