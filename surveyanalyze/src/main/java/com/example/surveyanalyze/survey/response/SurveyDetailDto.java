package com.example.surveyanalyze.survey.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyDetailDto {
    private Long id;
    private String title;
    private String description;
    //
    private int countAnswer;
    private List<QuestionDetailDto> questionList;

    // design 필요 없음
    String font;
    int fontSize;
    String backColor;
    // getter, setter 생략
}
