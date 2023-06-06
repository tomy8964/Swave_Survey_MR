package com.example.surveydocument.user.domain;

import com.example.surveydocument.survey.domain.Survey;
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

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private Survey survey;

    @CreationTimestamp //(4)
    private Timestamp createTime;
    private Boolean isDeleted = false;

    @Builder
    public User(Long id, String profileImg, String nickname,
                String email,String provider, String userRole,Boolean isDeleted) {

        this.id = id;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.email = email;
        this.provider=provider;
        this.userRole = userRole;
        this.isDeleted=isDeleted;
    }

}
