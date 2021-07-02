package ob1.eventmanager.statemachine.local.handler.event;

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
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Objects;

@Component("localEventTemplateHandler")
public class EventTemplateHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Autowired
    private TemplateService templateService;

    @Autowired
    private CategoryService categoryService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final int messageId = context.get("messageId");
        final String callbackQuery = context.get("callbackData");

        final LocalChatStates previousState = context.getPreviousState();


        if (previousState == LocalChatStates.EVENT_CATEGORY || previousState == LocalChatStates.EVENT_TEMPLATE_QUESTION) {
            final CategoryEntity category = event.getCategory();

            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Выбери шаблон:");

            List<TemplateEntity> templateEntities = category.getTemplates();
            InlineKeyboardMarkup templateKeyboardMarkup = new TemplateButtonBuilder().buildTemplateButtons(templateEntities);
            sendMessage.setReplyMarkup(templateKeyboardMarkup);

            bot.send(sendMessage);
        } else if (previousState == LocalChatStates.EVENT_TEMPLATE) {

            if (Objects.equals(text, "goBackToCategory") || Objects.equals(callbackQuery, "goBackToCategory")) {
                context.setNextState(LocalChatStates.EVENT_CATEGORY);
                return;
            }

            if (text != null) {

                try {
                    event = eventService.setEventTemplate(event, text);
                    context.getHeaders().put("event", event);

                    context.setNextState(LocalChatStates.EVENT_TEMPLATE_QUESTION);
                } catch (TemplateNotFoundException e) {
                    final SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("К сожалению, не могу найти подходящий шаблон в базе. Попробуй выбрать другой.");
                    bot.send(sendMessage);
                }
            }

            if (callbackQuery != null) {
                if (callbackQuery.equals("goBackToTemplate")) {
                    context.setNextState(LocalChatStates.EVENT_CATEGORY);
                    return;
                }
                try {
                    event = eventService.setEventTemplate(event, callbackQuery);
                    context.getHeaders().put("event", event);

                    context.setNextState(LocalChatStates.EVENT_TEMPLATE_QUESTION);
                } catch (TemplateNotFoundException e) {
                    final SendMessage sendMessage = new SendMessage();
                    sendMessage.setChatId(chatId);
                    sendMessage.setText("К сожалению, не могу найти подходящий шаблон в базе. Попробуй выбрать другой.");
                    bot.send(sendMessage);
                }
            }
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}