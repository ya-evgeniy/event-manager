package ob1.eventmanager.service;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.UserEntity;

import java.util.List;
import java.util.Optional;

public interface EventService {

    EventEntity getEventById(long id);

    List<EventEntity> getOwnerEvents(long chatId);

    EventEntity newEvent(UserEntity owner, long ownerChatId);

    Optional<EventEntity> getGroupEvent(long chatId);

    EventEntity setEventChatId(EventEntity event, long chatId);

    EventEntity setEventName(EventEntity event, String name);

    EventEntity setEventDate(EventEntity event, String date);

    EventEntity setEventPlace(EventEntity event, String place);

    EventEntity setEventCategory(EventEntity event, String category);

    EventEntity setEventTemplate(EventEntity event, String template);

    EventEntity verifyEvent(EventEntity event);

    void delete(EventEntity event);

}
