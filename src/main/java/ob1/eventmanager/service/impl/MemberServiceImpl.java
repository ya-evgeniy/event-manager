package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.repository.MemberRepository;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.service.UserService;
import ob1.eventmanager.utils.LocalDateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MemberServiceImpl implements MemberService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private LocalDateParser parser;

    @Autowired
    private UserService userService;


    @Override
    public Set<EventEntity> getEventsByTelegramId(int telegramId) {
        final Optional<UserEntity> optUserEntity = userService.getUserByTelegramId(telegramId);
        if (optUserEntity.isEmpty()) return Collections.emptySet();

        final UserEntity userEntity = optUserEntity.get();
        final Set<MemberEntity> members = memberRepository.findAllByUser(userEntity);

        return members.stream()
                .map(MemberEntity::getEvent)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<EventEntity> getActualEventsByTelegramId(int telegramId) {
        final LocalDateTime now = LocalDateTime.now();

        return getEventsByTelegramId(telegramId).stream()
                .filter(EventEntity::isVerified)
                .filter(event -> event.getDate() != null)
                .filter(event -> event.getDate().isAfter(now))
                .collect(Collectors.toSet());
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
