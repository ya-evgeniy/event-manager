package ob1.eventmanager.statemachine.event.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.categoryChooser.CategoryButtonBuilder;
import ob1.eventmanager.categoryChooser.TemplateButtonBuilder;
import ob1.eventmanager.entity.CategoryEntity;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateEntity;
import ob1.eventmanager.service.CategoryService;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.TemplateService;
import ob1.eventmanager.statemachine.event.EventEvents;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class EventQuestionActions {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private TemplateService templateService;

    public void waitCategory(StateContext<EventStates, EventEvents> context) {
        final MessageHeaders headers = context.getMessageHeaders();

        final String text = (String) headers.get("text");
        final String chatId = (String) headers.get("chatId");
        final EventEntity event = (EventEntity) headers.get("event");
        final int messageId = (int) headers.get("messageId");

        if (context.getMessage().getPayload() == EventEvents.CATEGORY_IS_INCORRECT) {
            final EditMessageText editMessage = new EditMessageText();

            editMessage.setText("Выберите категорию, к которой относится ваше мероприятие:");
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);

            InlineKeyboardMarkup categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtons(categoryService.getCategories());
            editMessage.setReplyMarkup(categoryKeyboardMarkup);

            bot.send(editMessage);
        }
        else {
            eventService.setEventPlace(event, text);
            bot.send("Так и запишем: " + text, chatId);

            final SendMessage sendMessage = new SendMessage();

            sendMessage.setText("Выберите категорию, к которой относится ваше мероприятие:");
            sendMessage.setChatId(chatId);

            InlineKeyboardMarkup categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtons(categoryService.getCategories());
            sendMessage.setReplyMarkup(categoryKeyboardMarkup);

            bot.send(sendMessage);
        }
    }

    public void categoryCheck(StateContext<EventStates, EventEvents> context) {
        System.out.println("categoryCheck");
        final MessageHeaders headers = context.getMessageHeaders();

        final String callbackData = (String) headers.get("callbackData");
        final String chatId = (String) headers.get("chatId");
        final int messageId = (int) headers.get("messageId");
        final EventEntity event = (EventEntity) headers.get("event");

        final long categoryId = Long.parseLong(callbackData.substring("cat".length()));
        final Optional<CategoryEntity> optCategory = categoryService.getById(categoryId);

        final CategoryEntity category = optCategory.get(); // fixme

        final EditMessageText editMessage = new EditMessageText();
        editMessage.setMessageId(messageId);
        editMessage.setChatId(chatId);
        editMessage.setText(category.getName() + " - то что нужно! Выбирай шаблон:");

        List<TemplateEntity> templateEntities =  templateService.getTemplatesByCategory(category);

        InlineKeyboardMarkup templateKeyboardMarkup = new TemplateButtonBuilder().buildTemplateButtons(templateEntities);
        editMessage.setReplyMarkup(templateKeyboardMarkup);

        bot.send(editMessage);
        final StateMachine<EventStates, EventEvents> stateMachine = context.getStateMachine();
        stateMachine.sendEvent(new GenericMessage<>(
                EventEvents.CATEGORY_IS_CORRECT,
                headers
        ));
    }

    public void waitTemplate(StateContext<EventStates, EventEvents> context) {
        System.out.println("waitTemplate");

    }

    public void templateCheck(StateContext<EventStates, EventEvents> context) {
        System.out.println("TEMPLATE CHECK");
        final MessageHeaders headers = context.getMessageHeaders();

        final String callbackData = (String) headers.get("callbackData");
        final String chatId = (String) headers.get("chatId");
        final int messageId = (int) headers.get("messageId");
        final EventEntity event = (EventEntity) headers.get("event");

        if (Objects.equals(callbackData, "backToCategoryList")) {
            final StateMachine<EventStates, EventEvents> stateMachine = context.getStateMachine();
            stateMachine.sendEvent(new GenericMessage<>(
                    EventEvents.TEMPLATE_IS_INCORRECT,
                    headers
            ));
            return;
        }
    }

    public void questionCheck(StateContext<EventStates, EventEvents> context) {
        System.out.println("QUESTION CHECK");
    }

    public void waitQuestionsConfirm(StateContext<EventStates, EventEvents> context) {
        System.out.println("WAIT QUESTION CONFIRM");
    }

}
