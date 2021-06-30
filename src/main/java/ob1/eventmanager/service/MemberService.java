package ob1.eventmanager.service;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;

import java.time.LocalDateTime;
import java.util.Set;

public interface MemberService {

    boolean hasMember(UserEntity user, EventEntity event);

    MemberEntity getMemberById(long id);

    MemberEntity getMember(UserEntity user, EventEntity event);

    MemberEntity createMember(UserEntity user, EventEntity event);

    void removeMember(MemberEntity memberEntity);

    Set<EventEntity> getEventsByTelegramId(int telegramId);

    Set<EventEntity> getActualEventsByTelegramId(int telegramId);

    MemberEntity setComfortPlace(MemberEntity member, String place);

    MemberEntity setComfortDate(MemberEntity member, String date);

    MemberEntity setAnnounceDate(MemberEntity member, LocalDateTime date);

    MemberEntity setAnnounceCount(MemberEntity member, int count);

    MemberEntity setCurrentQuestion(MemberEntity member, EventQuestionEntity question);
}
