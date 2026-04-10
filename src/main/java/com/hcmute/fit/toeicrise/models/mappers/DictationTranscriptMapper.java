package com.hcmute.fit.toeicrise.models.mappers;


import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationTranscriptRequest;
import com.hcmute.fit.toeicrise.dtos.requests.dictation.DictationTranscriptUpdateRequest;
import com.hcmute.fit.toeicrise.models.entities.DictationTranscript;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface DictationTranscriptMapper {
    @Mapping(target = "questionGroup", ignore = true)
    DictationTranscript toDictationTranscript(DictationTranscriptRequest request);
    DictationTranscript toDictationTranscript(DictationTranscriptUpdateRequest request);
}
