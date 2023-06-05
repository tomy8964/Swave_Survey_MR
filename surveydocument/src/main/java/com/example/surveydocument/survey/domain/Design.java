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
    @Column(name = "Design_id")
    private Long id;
    @Column(name = "font")
    private String font;
    @Column(name = "font_size")
    private int fontSize;
    @Column(name = "back_color")
    private String backColor;

    @Column(name = "SurveyDocument_id")
    @OneToOne(mappedBy = "design")
    private SurveyDocument surveyDocument;

    @Builder
    public Design(String font, int fontSize, String backColor) {
        this.font = font;
        this.fontSize = fontSize;
        this.backColor = backColor;
    }

    // Request -> Entity
    public static Design designRequestToEntity(String font, int fontSize, String backColor) {
        return Design.builder()
                .font(font)
                .fontSize(fontSize)
                .backColor(backColor)
                .build();
    }
}
