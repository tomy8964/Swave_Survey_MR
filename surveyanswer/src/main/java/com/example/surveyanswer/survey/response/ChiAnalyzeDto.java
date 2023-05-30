package com.example.surveyanswer.survey.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChiAnalyzeDto {
    private Long id;
    private String questionTitle;
    private Double pValue;
}
