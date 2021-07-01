package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.CategoryEntity;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.exception.CategoryNotFoundException;
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
import java.util.List;
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
    public EventEntity getEventById(long id) {
        return eventRepository.findById(id).orElseThrow(EventNotFoundException::new);
    }

    @Override
    public List<EventEntity> getOwnerEvents(long chatId) {
        return eventRepository.findAllByOwnerChatId(chatId);
    }

    @Override
    public EventEntity newEvent(UserEntity owner, long ownerChatId) {
        final EventEntity event = EventEntity.builder()
                .owner(owner)
                .ownerChatId(ownerChatId)
                .build();

        return eventRepository.save(event);
    }

    @Override
    public Optional<EventEntity> getGroupEvent(long chatId) {
        return eventRepository.getByChatId(chatId);
    }

    @Override
    public EventEntity setEventChatId(EventEntity event, long chatId) {
        final EventEntity eventEntity = EventEntity.builder()
                .id(event.getId())
                .chatId(chatId)
                .name(event.getName())
                .date(event.getDate())
                .place(event.getPlace())
                .verified(event.isVerified())
                .completed(event.isCompleted())
                .owner(event.getOwner())
                .ownerChatId(event.getOwnerChatId())
                .category(event.getCategory())
                .template(event.getTemplate())
                .build();

        return eventRepository.save(eventEntity);
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
                .completed(event.isCompleted())
                .owner(event.getOwner())
                .ownerChatId(event.getOwnerChatId())
                .category(event.getCategory())
                .template(event.getTemplate())
                .build();

        return eventRepository.save(eventEntity);
    }

    @Override
    public EventEntity setEventDate(EventEntity event, String date) {
        final LocalDateTime datetime = parser.parseDate(date);

        final EventEntity eventEntity = EventEntity.builder()
                .id(event.getId())
                .chatId(event.getChatId())
                .name(event.getName())
                .date(datetime)
                .place(event.getPlace())
                .verified(event.isVerified())
                .completed(event.isCompleted())
                .owner(event.getOwner())
                .ownerChatId(event.getOwnerChatId())
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
                .completed(event.isCompleted())
                .owner(event.getOwner())
                .ownerChatId(event.getOwnerChatId())
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
                    .completed(event.isCompleted())
                    .owner(event.getOwner())
                    .ownerChatId(event.getOwnerChatId())
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
                    .completed(event.isCompleted())
                    .owner(event.getOwner())
                    .ownerChatId(event.getOwnerChatId())
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
                .completed(event.isCompleted())
                .owner(event.getOwner())
                .ownerChatId(event.getOwnerChatId())
                .category(event.getCategory())
                .template(event.getTemplate())
                .build();

        return eventRepository.save(eventEntity);
    }

    @Override
    public void delete(EventEntity event) {

    }

}
