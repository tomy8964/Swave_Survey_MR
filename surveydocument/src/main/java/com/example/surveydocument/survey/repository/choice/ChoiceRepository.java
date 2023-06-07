package com.example.surveydocument.survey.repository.choice;

import com.example.surveydocument.survey.domain.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface ChoiceRepository extends JpaRepository<Choice, Long>, QuerydslPredicateExecutor<Choice>, ChoiceRepositoryCustom {
    @Modifying
    @Transactional
    @Query("UPDATE Choice c SET c.count = c.count + 1 WHERE c.id = :id")
    void incrementCount(@Param("id") Long id);
}
