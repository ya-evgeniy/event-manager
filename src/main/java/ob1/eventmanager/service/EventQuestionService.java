package ob1.eventmanager.service;

import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberEntity;

import java.util.List;
import java.util.Optional;

public interface EventQuestionService {

    List<EventQuestionEntity> getUnansweredQuestions(MemberEntity member);

    Optional<EventQuestionEntity> getUnansweredQuestion(MemberEntity member);

}
