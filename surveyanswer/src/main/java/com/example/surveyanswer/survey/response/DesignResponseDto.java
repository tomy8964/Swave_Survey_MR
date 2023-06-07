package com.example.surveyanswer.survey.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Data
@NoArgsConstructor
public class DesignResponseDto {
    private String font;
    private int fontSize;
    private String backColor;
}
