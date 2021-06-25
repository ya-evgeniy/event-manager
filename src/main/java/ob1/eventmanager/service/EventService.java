package ob1.eventmanager.service;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.UserEntity;

public interface EventService {

    EventEntity newEvent(UserEntity user, long chatId);

    EventEntity getEvent(long chatId);

    void setEventName(EventEntity event, String name);

    void setEventDate(EventEntity event, String date);

    void setEventPlace(EventEntity event, String place);

    void setEventCategory(EventEntity event, String category);

    void setEventTemplate(EventEntity event, String template);

    void verifyEvent(EventEntity event);

}
