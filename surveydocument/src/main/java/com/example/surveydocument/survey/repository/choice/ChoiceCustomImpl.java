package com.example.surveydocument.survey.repository.choice;

import com.example.surveydocument.survey.domain.Choice;
import com.example.surveydocument.survey.domain.QChoice;
import com.example.surveydocument.survey.domain.QSurveyDocument;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
public class ChoiceCustomImpl implements ChoiceRepositoryCustom{
    private final JPAQueryFactory jpaQueryFactory;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public void choiceCount(Choice choice) {
        QChoice qChoice = QChoice.choice;
        jpaQueryFactory
                .update(qChoice)
                .set(qChoice.count, qChoice.count.add(1))
                .where(qChoice.id.eq(choice.getId()))
                .execute();
        entityManager.flush();
        entityManager.clear();
    }
}
