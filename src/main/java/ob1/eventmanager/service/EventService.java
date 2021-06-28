package ob1.eventmanager.service;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.UserEntity;

import java.util.List;

public interface EventService {

    EventEntity newEvent(UserEntity user, long chatId);

    EventEntity getEvent(long chatId);

    EventEntity setEventName(EventEntity event, String name);

    EventEntity setEventDate(EventEntity event, String date);

    EventEntity setEventPlace(EventEntity event, String place);

    EventEntity setEventCategory(EventEntity event, String category);

    EventEntity setEventTemplate(EventEntity event, String template);

    EventEntity verifyEvent(EventEntity event);

}
