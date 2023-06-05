package com.example.surveydocument.survey.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Entity
@NoArgsConstructor
@Data
public class DateManagement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Date_id")
    private long id;

    @Column(name = "survey_start_date")
    @DateTimeFormat(pattern = "yy-mm-dd")
    private LocalDate startDate;

    @Column(name = "survey_deadline")
    @DateTimeFormat(pattern = "yy-mm-dd")
    private LocalDate deadline;

    @OneToOne(mappedBy = "date")
    private SurveyDocument surveyDocument;

}
