package com.example.user.user.domain;

import com.example.user.survey.domain.Survey;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "user_master")
public class User {


    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_Id")
    private Long userCode;
    private Long id;
    private String profileImg;

    private String nickname;

    private String email;

    private String provider;

    private String userRole;

    private String Description;

    @OneToOne(mappedBy = "user",fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JsonIgnore
    private Survey survey;

    @CreationTimestamp //(4)
    private Timestamp createTime;

    private Boolean isDeleted;

    @Builder
    public User(Long id, String profileImg, String nickname,
                String email,String provider, String userRole, Survey survey) {
        this.survey = survey;
        this.id = id;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.email = email;
        this.provider=provider;
        this.userRole = userRole;
        this.isDeleted=isDeleted;
    }

}
