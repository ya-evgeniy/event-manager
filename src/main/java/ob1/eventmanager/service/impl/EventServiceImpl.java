package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.CategoryEntity;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.exception.CategoryNotFoundException;
import ob1.eventmanager.exception.EventAlreadyExistsException;
import ob1.eventmanager.exception.EventNotFoundException;
import ob1.eventmanager.exception.TemplateNotFoundException;
import ob1.eventmanager.repository.CategoryRepository;
import ob1.eventmanager.repository.EventRepository;
import ob1.eventmanager.repository.TemplateRepository;
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
    private CategoryRepository categoryRepository;

    @Autowired
    private TemplateRepository templateRepository;

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
    public EventEntity setEventName(EventEntity event, String name) {
        final EventEntity eventEntity = EventEntity.builder()
                .id(event.getId())
                .chatId(event.getChatId())
                .name(name)
                .date(event.getDate())
                .place(event.getPlace())
                .verified(event.isVerified())
                .owner(event.getOwner())
                .category(event.getCategory())
                .template(event.getTemplate())
                .build();

        return eventRepository.save(eventEntity);
    }

    @Override
    public EventEntity setEventDate(EventEntity event, String date) {
        final LocalDateTime datetime = parser.parse(date);

        final EventEntity eventEntity = EventEntity.builder()
                .id(event.getId())
                .chatId(event.getChatId())
                .name(event.getName())
                .date(datetime)
                .place(event.getPlace())
                .verified(event.isVerified())
                .owner(event.getOwner())
                .category(event.getCategory())
                .template(event.getTemplate())
                .build();

        return eventRepository.save(eventEntity);
    }

    @Override
    public EventEntity setEventPlace(EventEntity event, String place) {
        final EventEntity eventEntity = EventEntity.builder()
                .id(event.getId())
                .chatId(event.getChatId())
                .name(event.getName())
                .date(event.getDate())
                .place(place)
                .verified(event.isVerified())
                .owner(event.getOwner())
                .category(event.getCategory())
                .template(event.getTemplate())
                .build();

        return eventRepository.save(eventEntity);
    }

    @Override
    public EventEntity setEventCategory(EventEntity event, String categoryName) {
        Optional<CategoryEntity> optCategory;
        if (categoryName.startsWith("cat")) {
            final long id = Long.parseLong(categoryName.substring("cat".length()));
            optCategory = categoryRepository.findById(id);
        }
        else {
            optCategory = categoryRepository.findFirstByNameLike(categoryName);
        }

        if (optCategory.isPresent()) {
            final EventEntity eventEntity = EventEntity.builder()
                    .id(event.getId())
                    .chatId(event.getChatId())
                    .name(event.getName())
                    .date(event.getDate())
                    .place(event.getPlace())
                    .verified(event.isVerified())
                    .owner(event.getOwner())
                    .category(optCategory.get())
                    .template(event.getTemplate())
                    .build();

            return eventRepository.save(eventEntity);
        }
        else {
            throw new CategoryNotFoundException(String.format("category with name '%s' not found", categoryName));
        }
    }

    @Override
    public EventEntity setEventTemplate(EventEntity event, String templateName) {
        Optional<TemplateEntity> optTemplate;
        if (templateName.startsWith("tem")) {
            final long id = Long.parseLong(templateName.substring("tem".length()));
            optTemplate = templateRepository.findById(id);
        }
        else {
            optTemplate = templateRepository.findFirstByNameLike(templateName);
        }

        if (optTemplate.isPresent()) {
            final EventEntity eventEntity = EventEntity.builder()
                    .id(event.getId())
                    .chatId(event.getChatId())
                    .name(event.getName())
                    .date(event.getDate())
                    .place(event.getPlace())
                    .verified(event.isVerified())
                    .owner(event.getOwner())
                    .category(event.getCategory())
                    .template(optTemplate.get())
                    .build();

            return eventRepository.save(eventEntity);
        }
        else {
            throw new TemplateNotFoundException(String.format("template with name '%s' not found", templateName));
        }
    }

    @Override
    public EventEntity verifyEvent(EventEntity event) {
        final EventEntity eventEntity = EventEntity.builder()
                .id(event.getId())
                .chatId(event.getChatId())
                .name(event.getName())
                .date(event.getDate())
                .place(event.getPlace())
                .verified(true)
                .owner(event.getOwner())
                .category(event.getCategory())
                .template(event.getTemplate())
                .build();

        return eventRepository.save(eventEntity);
    }

}
