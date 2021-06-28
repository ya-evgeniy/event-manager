package ob1.eventmanager.service;

import ob1.eventmanager.entity.EventEntity;

import java.util.Set;

public interface MemberService {

    Set<EventEntity> getEventsByTelegramId(int telegramId);

    Set<EventEntity> getActualEventsByTelegramId(int telegramId);

}
