package com.example.surveydocument.survey.request;

import lombok.Builder;
import lombok.Data;

@Data
public class DateDto {
    String startDate;
    String endDate;

    @Builder
    public DateDto(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
