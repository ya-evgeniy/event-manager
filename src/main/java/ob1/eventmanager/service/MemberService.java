package ob1.eventmanager.service;

import ob1.eventmanager.entity.EventEntity;

import java.util.List;

public interface MemberService {

    List<EventEntity> getEventsByTelegramId(int telegramId);

}
