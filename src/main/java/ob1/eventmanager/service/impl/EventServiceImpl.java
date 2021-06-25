package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.exception.EventAlreadyExistsException;
import ob1.eventmanager.exception.EventNotFoundException;
import ob1.eventmanager.repository.EventRepository;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.utils.LocalDateParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class EventServiceImpl implements EventService {

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private LocalDateParser parser;

    @Override
    public EventEntity newEvent(UserEntity user, long chatId) {
        final Optional<EventEntity> optEvent = eventRepository.getByChatId(chatId);
        if (optEvent.isPresent()) {
            throw new EventAlreadyExistsException(String.format("Event with chat id '%s' already exists", chatId));
        }

        final EventEntity event = EventEntity.builder()
                .owner(user)
                .chatId(chatId)
                .build();

        return eventRepository.save(event);
    }

    @Override
    public EventEntity getEvent(long chatId) {
        return eventRepository.getByChatId(chatId).orElseThrow(
                () -> new EventNotFoundException(String.format("Event with chat id '%s' not found", chatId))
        );
    }

    @Override
    public void setEventName(EventEntity event, String name) {
        final EventEntity eventEntity = EventEntity.builder()
                .id(event.getId())
                .chatId(event.getChatId())
                .name(name)
                .date(event.getDate())
                .place(event.getPlace())
                .verified(event.isVerified())
                .owner(event.getOwner())
                .build();

        eventRepository.save(eventEntity);
    }

    @Override
    public void setEventDate(EventEntity event, String date) {
        final LocalDateTime datetime = parser.parse(date);

        final EventEntity eventEntity = EventEntity.builder()
                .id(event.getId())
                .chatId(event.getChatId())
                .name(event.getName())
                .date(datetime)
                .place(event.getPlace())
                .verified(event.isVerified())
                .owner(event.getOwner())
                .build();

        eventRepository.save(eventEntity);
    }

    @Override
    public void setEventPlace(EventEntity event, String place) {
        final EventEntity eventEntity = EventEntity.builder()
                .id(event.getId())
                .chatId(event.getChatId())
                .name(event.getName())
                .date(event.getDate())
                .place(place)
                .verified(event.isVerified())
                .owner(event.getOwner())
                .build();

        eventRepository.save(eventEntity);
    }

    @Override
    public void verifyEvent(EventEntity event) {
        final EventEntity eventEntity = EventEntity.builder()
                .id(event.getId())
                .chatId(event.getChatId())
                .name(event.getName())
                .date(event.getDate())
                .place(event.getPlace())
                .verified(true)
                .owner(event.getOwner())
                .build();

        eventRepository.save(eventEntity);
    }

}
