package com.example.surveydocument.survey.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Entity
@NoArgsConstructor
@Data
public class DateManagement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Date_id")
    private long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP) @Column(name = "survey_start_date")
    private Date startDate;
    @CreationTimestamp @Temporal(TemporalType.TIMESTAMP) @Column(name = "survey_deadline")
    private Date deadline;

    @OneToOne(mappedBy = "date")
    private SurveyDocument surveyDocument;

}
