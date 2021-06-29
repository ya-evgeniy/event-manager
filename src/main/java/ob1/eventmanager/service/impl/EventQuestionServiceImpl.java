package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberAnswerEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.service.EventQuestionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EventQuestionServiceImpl implements EventQuestionService {

    @Override
    public List<EventQuestionEntity> getUnansweredQuestions(MemberEntity member) {
        final List<EventQuestionEntity> questions = new ArrayList<>(member.getEvent().getQuestions());
        final List<EventQuestionEntity> answeredQuestions = member.getAnswers()
                .stream()
                .map(MemberAnswerEntity::getQuestion)
                .collect(Collectors.toList());

        questions.removeAll(answeredQuestions);
        return questions;
    }

    @Override
    public Optional<EventQuestionEntity> getUnansweredQuestion(MemberEntity member) {
        final List<EventQuestionEntity> questions = getUnansweredQuestions(member);
        if (questions.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(questions.get(0));
    }

}
