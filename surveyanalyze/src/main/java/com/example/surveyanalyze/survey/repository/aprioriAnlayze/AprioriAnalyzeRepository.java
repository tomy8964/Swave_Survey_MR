package com.example.surveyanalyze.survey.repository.aprioriAnlayze;

import com.example.surveyanalyze.survey.domain.AprioriAnalyze;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AprioriAnalyzeRepository  extends JpaRepository<AprioriAnalyze, Long>, AprioriAnalyzeRepositoryCustom {

}