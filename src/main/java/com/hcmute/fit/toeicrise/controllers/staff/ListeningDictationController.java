package com.hcmute.fit.toeicrise.controllers.staff;

import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationImportRequest;
import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationTranscriptUpdateRequest;
import com.hcmute.fit.toeicrise.services.interfaces.IDictationTranscriptService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController("staffListeningDictationController")
@RequestMapping("/staff/dictation")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class ListeningDictationController {
    IDictationTranscriptService dictationTranscriptService;

    @PostMapping("import")
    public ResponseEntity<Void> saveDictationTranscripts(@Valid @RequestBody DictationImportRequest request) {
        dictationTranscriptService.importDictationTranscript(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("test/{testId}/part/{partId}")
    public ResponseEntity<?> getListeningDictationData(@PathVariable Long testId, @PathVariable Long partId) {
        return ResponseEntity.ok(
                dictationTranscriptService.getListeningDictationByTestAndPart(testId, partId));
    }

    @PutMapping()
    public ResponseEntity<Void> updateDictationTranscripts(@Valid @RequestBody DictationTranscriptUpdateRequest request) {
        dictationTranscriptService.updateDictationTranscript(request);
        return ResponseEntity.ok().build();
    }
}
