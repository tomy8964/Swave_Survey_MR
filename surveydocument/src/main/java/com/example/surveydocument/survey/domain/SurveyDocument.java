package com.example.surveydocument.survey.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class SurveyDocument {

    //todo : soft delete 쿼리 조회 되게 만들어주기

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_document_id")
    private Long id;
    @Column(name = "survey_title")
    private String title;
    @Column(name = "survey_type")
    private int type;
    @Column(name = "survey_description")
    private String description;
    @Column(name = "accept_response")
    private boolean acceptResponse;

    @Column(name = "url")
    private String url;
    @Column(name = "answer_count")
    private int countAnswer = 0;

    private boolean isDeleted = false;

    @OneToOne
    @JoinColumn(name = "Date_id")
    private DateManagement date;

    @Column(name = "content")
    @OneToMany(mappedBy = "surveyDocumentId", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<QuestionDocument> questionDocumentList;
    @ManyToOne
    @JsonIgnore // 순환참조 방지
    @JoinColumn(name = "survey_id")
    private Survey survey;

    @Builder
    public SurveyDocument(int countAnswer, List<SurveyAnswer> surveyAnswerList, Survey survey, String title, int type, String description, List<QuestionDocument> questionDocumentList) {
        this.survey = survey;
        this.title = title;
        this.type = type;
        this.description = description;
        this.questionDocumentList = questionDocumentList;
//        this.surveyAnswerList = surveyAnswerList;
        this.countAnswer = countAnswer;
    }

//    public void setAnswer(SurveyAnswer surveyAnswer) {
//        this.surveyAnswerList.add(surveyAnswer);
//    }
    // 문항 list 에 넣어주기
    public void setQuestion(QuestionDocument questionDocument) {
        this.questionDocumentList.add(questionDocument);
    }
    // 문항 analyze 에 넣어주기
//    public void setAnalyze(surveyAnswer surveyAnswer) {
//        this.surveyAnswer=surveyAnswer;
//    }

    // todo : QueryDSL 문제 해결하기
    // 조회수 추가 dirty 방식
    public void updateAnswerCount(int countAnswer) {
        this.countAnswer = countAnswer + 1;
    }
}
