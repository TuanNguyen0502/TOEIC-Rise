package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationTranscriptRequest;
import com.hcmute.fit.toeicrise.models.mappers.DictationTranscriptMapper;
import com.hcmute.fit.toeicrise.services.interfaces.IDictationTranscriptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("staffListeningDictationController")
@RequestMapping("/staff/dictation")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ListeningDictationController {
    IDictationTranscriptService dictationTranscriptService;

    @PostMapping("import")
    public ResponseEntity<Void> saveDictationTranscripts(@Valid @RequestBody List<DictationTranscriptRequest> requests) {
        dictationTranscriptService.importDictationTranscript(requests);
        return ResponseEntity.ok().build();
    }
}
