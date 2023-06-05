package com.example.surveydocument.survey.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Design {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String font;
    private int fontSize;
    private String backColor;

    @OneToOne(mappedBy = "design")
    private SurveyDocument surveyDocument;

    @Builder
    public Design(String font, int fontSize, String backColor) {
        this.font = font;
        this.fontSize = fontSize;
        this.backColor = backColor;
    }

    // Request -> Entity
    public static void designRequestToEntity(String font, int fontSize, String backColor) {
        Design design = Design.builder()
                .font(font)
                .fontSize(fontSize)
                .backColor(backColor)
                .build();
    }
}
