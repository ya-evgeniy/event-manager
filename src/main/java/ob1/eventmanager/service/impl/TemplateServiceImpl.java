package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.CategoryEntity;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionAnswerEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.TemplateEntity;
import ob1.eventmanager.entity.TemplateQuestionAnswerEntity;
import ob1.eventmanager.entity.TemplateQuestionEntity;
import ob1.eventmanager.repository.EventQuestionAnswerRepository;
import ob1.eventmanager.repository.EventQuestionRepository;
import ob1.eventmanager.repository.TemplateRepository;
import ob1.eventmanager.service.TemplateService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private EventQuestionRepository eventQuestionRepository;

    @Autowired
    private EventQuestionAnswerRepository eventQuestionAnswerRepository;

    @Override
    public List<TemplateEntity> getTemplatesByCategory(CategoryEntity category) {
        Hibernate.initialize(category.getTemplates());
        return category.getTemplates();
    }

    @Override
    public void copyTemplateToEvent(TemplateEntity template, EventEntity event) {
        for (TemplateQuestionEntity question : template.getQuestions()) {
            final EventQuestionEntity eventQuestion = eventQuestionRepository.save(EventQuestionEntity.builder()
                    .event(event)
                    .question(question.getQuestion())
                    .build());

            Hibernate.initialize(question.getAnswers());
            for (TemplateQuestionAnswerEntity answer : question.getAnswers()) {
                eventQuestionAnswerRepository.save(EventQuestionAnswerEntity.builder()
                        .question(eventQuestion)
                        .answer(answer.getAnswer())
                        .build());
            }
        }
    }

    @Override
    public Optional<TemplateEntity> getTemplateById(long id) {
        return templateRepository.findById(id);
    }

    @Override
    public Optional<TemplateEntity> getTemplateByName(String name) {
        return templateRepository.findFirstByNameLike(name);
    }

}
