package com.example.user.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
public class SurveyAnalyze {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "survey_analyze_id")
    private Long id;

    @Column(name = "survey_document_id")
    private Long surveyDocumentId;

    @OneToMany(mappedBy = "surveyAnswerId", fetch = FetchType.LAZY, orphanRemoval = true)
    @Column(name = "연관분석")
    private List<QuestionAnalyze> questionAnalyzeList;


    @Builder
    public SurveyAnalyze(List<QuestionAnalyze> questionAnalyzeList, Long surveyDocumentId) {
        this.questionAnalyzeList = questionAnalyzeList;
        this.surveyDocumentId = surveyDocumentId;
    }
}
