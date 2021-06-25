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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Optional;

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
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final EventEntity event = context.get("event");
        final int messageId = context.get("messageId");
        final String callbackQuery = context.get("callbackData");

        final EventStates previousState = context.getPreviousState();


        if (previousState == EventStates.CATEGORY) {
            int categoryId = 0; //fixme get from db
            final Optional<CategoryEntity> optCategory = categoryService.getById(categoryId);
            final CategoryEntity category = optCategory.get();//fixme asked he somewhen...

            final EditMessageText editMessage = new EditMessageText();
            editMessage.setMessageId(messageId);
            editMessage.setChatId(chatId);

            List<TemplateEntity> templateEntities = templateService.getTemplatesByCategory(category);
            InlineKeyboardMarkup templateKeyboardMarkup = new TemplateButtonBuilder().buildTemplateButtons(templateEntities);
            editMessage.setReplyMarkup(templateKeyboardMarkup);

            bot.send(editMessage);

        } else if (previousState == EventStates.TEMPLATE) {

            if (text != null) {
                try {
                    eventService.setEventTemplate(event, text);
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
                    eventService.setEventTemplate(event, callbackQuery);
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