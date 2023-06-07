package com.example.surveydocument.survey.repository.survey;

import com.example.surveydocument.survey.domain.Survey;
import com.example.surveydocument.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyRepository extends JpaRepository<Survey, Long>, SurveyRepositoryCustom {
    Survey findByUserCode(Long userCode);
}