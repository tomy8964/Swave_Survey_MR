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

    @CreationTimestamp //(4)
    private Timestamp createTime;

    private boolean isDeleted = false;

    @Builder
    public User(Long id, String profileImg, String nickname,
                String email,String provider, String userRole) {
        this.id = id;
        this.profileImg = profileImg;
        this.nickname = nickname;
        this.email = email;
        this.provider=provider;
        this.userRole = userRole;
    }

    public void setIsDeleted(boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

}
