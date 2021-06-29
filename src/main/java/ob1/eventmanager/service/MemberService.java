package ob1.eventmanager.service;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.MemberEntity;

import java.util.Set;

public interface MemberService {

    Set<EventEntity> getEventsByTelegramId(int telegramId);

    Set<EventEntity> getActualEventsByTelegramId(int telegramId);

    MemberEntity setComfortPlace(MemberEntity member, String place);

    MemberEntity setComfortDate(MemberEntity member, String date);

}
