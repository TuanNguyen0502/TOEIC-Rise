package com.hcmute.fit.toeicrise.services.interfaces;

import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.QuestionRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.SpeakingQuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.requests.question.WritingQuestionExcelRequest;
import com.hcmute.fit.toeicrise.dtos.responses.comment.TaggedQuestionDetailResponse;
import com.hcmute.fit.toeicrise.dtos.responses.question.QuestionMapResponse;
import com.hcmute.fit.toeicrise.models.entities.Question;
import com.hcmute.fit.toeicrise.models.entities.QuestionGroup;
import com.hcmute.fit.toeicrise.dtos.responses.test.QuestionResponse;
import com.hcmute.fit.toeicrise.models.entities.Test;

import java.util.List;
import java.util.Set;

public interface IQuestionService {
    List<QuestionResponse> getQuestionsByQuestionGroupId(Long questionGroupId);

    void updateQuestion(QuestionRequest questionRequest);

    QuestionResponse getQuestionResponseById(Long questionId);

    List<Question> getQuestionEntitiesByIds(List<Long> questionIds);

    Question findById(Long aLong);

    void updateQuestionWithEntity(Question question, QuestionRequest request);

    List<Question> getAllQuestionsByPartAndTags(Set<Long> tagIds, Long partId);

    List<Question> getQuestionsWithGroupsByIds(List<Long> questionIds);

    void validateQuestion(List<Long> questionIds, List<Question> questions);

    List<Question> findAllQuestionByIdWithTags(Set<Long> questionIds);

    void createQuestionBatch(List<QuestionExcelRequest> questions, QuestionGroup questionGroup);

    void createSpeakingQuestionBatch(List<SpeakingQuestionExcelRequest> questionExcelRequests, QuestionGroup questionGroup);

    void createWritingQuestionBatch(List<WritingQuestionExcelRequest> questionExcelRequests, QuestionGroup questionGroup);

    void changeTestStatusToPending(Test test);

    List<QuestionMapResponse> getQuestionByTestId(Long testId);

    TaggedQuestionDetailResponse getTaggedQuestionDetail(Long questionId);
}