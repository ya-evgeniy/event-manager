package ob1.eventmanager.statemachine.event.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.categoryChooser.TemplateButtonBuilder;
import ob1.eventmanager.entity.CategoryEntity;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateEntity;
import ob1.eventmanager.exception.TemplateNotFoundException;
import ob1.eventmanager.service.CategoryService;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.TemplateService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Objects;

@Component("eventTemplateHandler")
public class EventTemplateHandler implements MessageStateMachineHandler<EventStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void handle(MessageStateMachineContext<EventStates> context) {
        System.out.println(context.getPreviousState() + " -> " + context.getCurrentState());

        EventEntity event = context.get("event");
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final int messageId = context.get("messageId");
        final String callbackQuery = context.get("callbackData");

        final EventStates previousState = context.getPreviousState();


        if (previousState == EventStates.CATEGORY || previousState == EventStates.TEMPLATE_QUESTIONS) {
            final CategoryEntity category = event.getCategory();

            final EditMessageText editMessage = new EditMessageText();
            editMessage.setMessageId(messageId);
            editMessage.setChatId(chatId);

            editMessage.setText("Выберите шаблон:");

            List<TemplateEntity> templateEntities = category.getTemplates();
            InlineKeyboardMarkup templateKeyboardMarkup = new TemplateButtonBuilder().buildTemplateButtons(templateEntities);
            editMessage.setReplyMarkup(templateKeyboardMarkup);

            bot.send(editMessage);
        } else if (previousState == EventStates.TEMPLATE) {

            if (Objects.equals(text, "goBackToCategory") || Objects.equals(callbackQuery, "goBackToCategory")) {
                context.setNextState(EventStates.CATEGORY);
                return;
            }

            if (text != null) {

                try {
                    event = eventService.setEventTemplate(event, text);
                    context.getHeaders().put("event", event);

                    context.setNextState(EventStates.TEMPLATE_QUESTIONS);
                } catch (TemplateNotFoundException e) {
                    final EditMessageText editMessage = new EditMessageText();
                    editMessage.setMessageId(messageId);
                    editMessage.setChatId(chatId);
                    editMessage.setText("К сожалению, не могу найти подходящий шаблон в базе. Вы можете выбрать другой.");
                    bot.send(editMessage);
                }
            }

            if (callbackQuery != null) {
                if (callbackQuery.equals("goBackToTemplate")) {
                    context.setNextState(EventStates.CATEGORY);
                    return;
                }
                try {
                    event = eventService.setEventTemplate(event, callbackQuery);
                    context.getHeaders().put("event", event);

                    context.setNextState(EventStates.TEMPLATE_QUESTIONS);
                } catch (TemplateNotFoundException e) {
                    final EditMessageText editMessage = new EditMessageText();
                    editMessage.setMessageId(messageId);
                    editMessage.setChatId(chatId);
                    editMessage.setText("К сожалению, не могу найти подходящий шаблон в базе. Вы можете выбрать другой.");
                    bot.send(editMessage);
                }
            }
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}