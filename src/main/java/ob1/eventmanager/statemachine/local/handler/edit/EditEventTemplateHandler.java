package ob1.eventmanager.statemachine.local.handler.edit;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.categoryChooser.CategoryButtonBuilder;
import ob1.eventmanager.categoryChooser.TemplateButtonBuilder;
import ob1.eventmanager.entity.CategoryEntity;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateEntity;
import ob1.eventmanager.exception.CategoryNotFoundException;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Objects;

@Component("localEditEventTemplateHandler")
public class EditEventTemplateHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final CategoryEntity category = event.getCategory();
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final int messageId = context.get("messageId");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.EDIT_EVENT_CATEGORY) {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText("Выберите шаблон:");
            List<TemplateEntity> templateEntities = category.getTemplates();
            InlineKeyboardMarkup templateKeyboardMarkup = new TemplateButtonBuilder().buildTemplateButtonsWithCancelButton(templateEntities);
            sendMessage.setReplyMarkup(templateKeyboardMarkup);
            bot.send(sendMessage);
        } else if (previousState == LocalChatStates.EDIT_EVENT_TEMPLATE) {
            final String callbackData = context.get("callbackData");
            if (Objects.equals(callbackData, "cancel")) {
                context.setNextState(LocalChatStates.EDIT_EVENT_SHOW);
                return;
            }
            try {
                event = eventService.setEventTemplate(event, callbackData);
                context.getHeaders().put("event", event);
            } catch (TemplateNotFoundException e) {
                EditMessageText editMessage = new EditMessageText();
                editMessage.setMessageId(context.get("messageId"));
                editMessage.setChatId(chatId);
                editMessage.setText("Такой категории нет, выбери другую");
                bot.send(editMessage);
            }
            context.setNextState(LocalChatStates.EDIT_EVENT_TEMPLATE_QUESTION);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
