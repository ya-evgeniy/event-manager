package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberAnswerEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.repository.MemberAnswerRepository;
import ob1.eventmanager.service.MemberAnswerService;
import ob1.eventmanager.service.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberAnswerServiceImpl implements MemberAnswerService {

    @Autowired
    private MemberAnswerRepository repository;

    @Autowired
    private MemberService memberService;

    @Override
    public MemberEntity setAnswer(MemberEntity member, String answer) {
        MemberAnswerEntity memberAnswer = MemberAnswerEntity.builder()
                .answer(answer)
                .member(member)
                .question(member.getCurrentQuestion())
                .build();

        repository.save(memberAnswer);
        return memberService.getMemberById(member.getId());
    }

    @Override
    public Optional<MemberAnswerEntity> getAnswer(MemberEntity member, EventQuestionEntity question) {
        for (MemberAnswerEntity answer : member.getAnswers()) {
            if (answer.getQuestion().getId() == question.getId()) return Optional.of(answer);
        }
        return Optional.empty();
    }

}
