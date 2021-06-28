package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.repository.MemberRepository;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.utils.LocalDateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LocalDateParser parser;

    @Override
    public List<EventEntity> getEventsByTelegramId(int telegramId) {
        return null;
    }

    @Override
    public MemberEntity setComfortPlace(MemberEntity member, String place) {
        MemberEntity memberEntity = MemberEntity.builder()
                .id(member.getId())
                .user(member.getUser())
                .event(member.getEvent())
                .comfortDate(member.getComfortDate())
                .comfortPlace(place)
                .build();
        return memberRepository.save(memberEntity);
    }

    @Override
    public MemberEntity setComfortDate(MemberEntity member, String date) {
        final LocalDateTime datetime = parser.parse(date);
        MemberEntity memberEntity = MemberEntity.builder()
                .id(member.getId())
                .user(member.getUser())
                .event(member.getEvent())
                .comfortDate(datetime)
                .comfortPlace(member.getComfortPlace())
                .build();
        return memberRepository.save(memberEntity);
    }

}
