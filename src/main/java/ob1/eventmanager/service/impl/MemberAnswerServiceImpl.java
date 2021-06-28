package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.MemberAnswerEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.repository.MemberAnswerRepository;
import ob1.eventmanager.service.MemberAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemberAnswerServiceImpl implements MemberAnswerService {

    @Autowired
    private MemberAnswerRepository repository;

    @Override
    public void setAnswer(MemberEntity member, String answer) {
        MemberAnswerEntity memberAnswer = MemberAnswerEntity.builder()
                .answer(answer)
                .member(member)
                .question(member.getCurrentQuestion())
                .build();

        repository.save(memberAnswer);
    }

}
