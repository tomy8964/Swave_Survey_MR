package com.example.surveyanswer.survey.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SurveyDetailDto implements Serializable {
    private Long id;
    private String title;
    private String description;
    private int countAnswer;
    private List<QuestionDetailDto> questionList;
    Boolean reliability;



    // 설문 참여 부분이기 때문에 디자인 필요
    String font;
    int fontSize;
    String backColor;
    // getter, setter 생략

}
