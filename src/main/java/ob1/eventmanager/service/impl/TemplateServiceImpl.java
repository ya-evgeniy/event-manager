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
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.TemplateService;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TemplateServiceImpl implements TemplateService {

    @Autowired
    private TemplateRepository templateRepository;

    @Autowired
    private EventQuestionRepository eventQuestionRepository;

    @Autowired
    private EventQuestionAnswerRepository eventQuestionAnswerRepository;

    @Autowired
    private EventService eventService;

    @Override
    public List<TemplateEntity> getTemplatesByCategory(CategoryEntity category) {
        Hibernate.initialize(category.getTemplates());
        return category.getTemplates();
    }

    @Override
    public EventEntity copyTemplateToEvent(TemplateEntity template, EventEntity event) {
        for (TemplateQuestionEntity question : template.getQuestions()) {
            final EventQuestionEntity eventQuestion = eventQuestionRepository.save(EventQuestionEntity.builder()
                    .event(event)
                    .question(question.getQuestion())
                    .build());

            for (TemplateQuestionAnswerEntity answer : question.getAnswers()) {
                eventQuestionAnswerRepository.save(EventQuestionAnswerEntity.builder()
                        .question(eventQuestion)
                        .answer(answer.getAnswer())
                        .build());
            }
        }

        return eventService.getEventById(event.getId());
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
