package ob1.eventmanager.service;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.UserEntity;

public interface EventService {

    EventEntity newEvent(UserEntity user, int chatId);

    EventEntity getEvent(int chatId);

    void setEventName(EventEntity event, String name);

    void setEventDate(EventEntity event, String date);

    void setEventPlace(EventEntity event, String place);

    void verifyEvent(EventEntity event);

}
