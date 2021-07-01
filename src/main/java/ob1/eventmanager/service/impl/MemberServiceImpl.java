package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.repository.MemberRepository;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.service.UserService;
import ob1.eventmanager.utils.LocalDateParser;
import ob1.eventmanager.utils.MemberStatus;
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
    public boolean hasMember(UserEntity user, EventEntity event) {
        return memberRepository.existsByUserAndEvent(user, event);
    }

    @Override
    public MemberEntity getMemberById(long id) {
        return memberRepository.findById(id).orElseThrow(UnsupportedOperationException::new);
    }

    @Override
    public MemberEntity getMember(UserEntity user, EventEntity event) {
        return memberRepository.findByUserAndEvent(user, event).orElseThrow(() -> {
            throw new UnsupportedOperationException(user.getId() + " " + event.getId());
        });
    }

    @Override
    public MemberEntity createMember(UserEntity user, EventEntity event) {
        final MemberEntity memberEntity = MemberEntity.builder()
                .user(user)
                .event(event)
                .status(MemberStatus.WAIT_PRIVATE_MESSAGE)
                .build();
        return memberRepository.save(memberEntity);
    }

    @Override
    public void removeMember(MemberEntity memberEntity) {
        memberRepository.removeById(memberEntity.getId());
    }

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
                .comfortTime(member.getComfortTime())
                .comfortPlace(place)
                .announceDate(member.getAnnounceDate())
                .announceCount(member.getAnnounceCount())
                .status(member.getStatus())
                .currentQuestion(member.getCurrentQuestion())
                .build();
        return memberRepository.save(memberEntity);
    }

    @Override
    public MemberEntity setComfortDate(MemberEntity member, String date) {
        final LocalDateTime comfortDate = parser.parseDate(date);
        MemberEntity memberEntity = MemberEntity.builder()
                .id(member.getId())
                .user(member.getUser())
                .event(member.getEvent())
                .comfortDate(comfortDate)
                .comfortTime(member.getComfortTime())
                .comfortPlace(member.getComfortPlace())
                .announceDate(member.getAnnounceDate())
                .announceCount(member.getAnnounceCount())
                .status(member.getStatus())
                .currentQuestion(member.getCurrentQuestion())
                .build();
        return memberRepository.save(memberEntity);
    }

    @Override
    public MemberEntity setComfortTime(MemberEntity member, String time) {
        final LocalDateTime comfortTime = parser.parseTime(time);
        MemberEntity memberEntity = MemberEntity.builder()
                .id(member.getId())
                .user(member.getUser())
                .event(member.getEvent())
                .comfortDate(member.getComfortDate())
                .comfortTime(comfortTime)
                .comfortPlace(member.getComfortPlace())
                .announceDate(member.getAnnounceDate())
                .announceCount(member.getAnnounceCount())
                .status(member.getStatus())
                .currentQuestion(member.getCurrentQuestion())
                .build();
        return memberRepository.save(memberEntity);
    }

    @Override
    public MemberEntity setAnnounceDate(MemberEntity member, LocalDateTime date) {
        MemberEntity memberEntity = MemberEntity.builder()
                .id(member.getId())
                .user(member.getUser())
                .event(member.getEvent())
                .comfortDate(member.getComfortDate())
                .comfortTime(member.getComfortTime())
                .comfortPlace(member.getComfortPlace())
                .announceDate(date)
                .announceCount(member.getAnnounceCount())
                .status(member.getStatus())
                .currentQuestion(member.getCurrentQuestion())
                .build();

        return memberRepository.save(memberEntity);
    }

    @Override
    public MemberEntity setAnnounceCount(MemberEntity member, int count) {
        MemberEntity memberEntity = MemberEntity.builder()
                .id(member.getId())
                .user(member.getUser())
                .event(member.getEvent())
                .comfortDate(member.getComfortDate())
                .comfortTime(member.getComfortTime())
                .comfortPlace(member.getComfortPlace())
                .announceDate(member.getAnnounceDate())
                .announceCount(count)
                .status(member.getStatus())
                .currentQuestion(member.getCurrentQuestion())
                .build();

        return memberRepository.save(memberEntity);
    }

    @Override
    public MemberEntity setStatus(MemberEntity member, MemberStatus status) {
        MemberEntity memberEntity = MemberEntity.builder()
                .id(member.getId())
                .user(member.getUser())
                .event(member.getEvent())
                .comfortDate(member.getComfortDate())
                .comfortTime(member.getComfortTime())
                .comfortPlace(member.getComfortPlace())
                .announceDate(member.getAnnounceDate())
                .announceCount(member.getAnnounceCount())
                .status(status)
                .currentQuestion(member.getCurrentQuestion())
                .build();

        return memberRepository.save(memberEntity);
    }

    @Override
    public MemberEntity setCurrentQuestion(MemberEntity member, EventQuestionEntity question) {
        MemberEntity memberEntity = MemberEntity.builder()
                .id(member.getId())
                .user(member.getUser())
                .event(member.getEvent())
                .comfortDate(member.getComfortDate())
                .comfortTime(member.getComfortTime())
                .comfortPlace(member.getComfortPlace())
                .announceDate(member.getAnnounceDate())
                .announceCount(member.getAnnounceCount())
                .status(member.getStatus())
                .currentQuestion(question)
                .build();

        return memberRepository.save(memberEntity);
    }

}
