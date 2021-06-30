package ob1.eventmanager.service;

import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberAnswerEntity;
import ob1.eventmanager.entity.MemberEntity;

import java.util.Optional;

public interface MemberAnswerService {

    MemberEntity setAnswer(MemberEntity member, String answer);

    Optional<MemberAnswerEntity> getAnswer(MemberEntity member, EventQuestionEntity question);

}

