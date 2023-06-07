package com.example.surveydocument.survey.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.hibernate.annotations.CreationTimestamp;
import org.jetbrains.annotations.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Data
@NoArgsConstructor
public class DateManagement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Date_id")
    private long id;

    @Column(name = "survey_start_date")
    @DateTimeFormat(pattern = "yy-mm-dd")
    @NotNull
    private LocalDate startDate;

    @Column(name = "survey_deadline")
    @DateTimeFormat(pattern = "yy-mm-dd")
    @NotNull
    private LocalDate deadline;

    // todo : enable boolean 추가

    @Builder
    public DateManagement(String startDate, String deadline) {
        this.startDate = LocalDate.parse(startDate);
        this.deadline = LocalDate.parse(deadline);
    }

    // RequestDto -> Entity
    public static DateManagement dateRequestToEntity(String start, String end) {
        return DateManagement.builder()
                .startDate(start)
                .deadline(end)
                .build();
    }
}
