package ob1.eventmanager.statemachine.local.handler.edit;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.categoryChooser.CategoryButtonBuilder;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.exception.CategoryNotFoundException;
import ob1.eventmanager.service.CategoryService;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.Objects;

@Component("localEditEventCategoryHandler")
public class EditEventCategoryHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EventService eventService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final int messageId = context.get("messageId");
        final String callbackData = context.get("callbackData");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.WAIT_COMMANDS) {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Выберите категорию, к которой относится ваше мероприятие:");
            sendMessage.setChatId(chatId);
            InlineKeyboardMarkup categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtonsWithCancelButton(categoryService.getCategories());
            sendMessage.setReplyMarkup(categoryKeyboardMarkup);
            bot.send(sendMessage);
        } else if (previousState == LocalChatStates.EDIT_EVENT_CATEGORY) {
            if (callbackData == null) {
                bot.send("Выбери из того что есть", chatId);
                return;
            }
            if (Objects.equals(callbackData, "cancel")) {
                context.setNextState(LocalChatStates.EDIT_EVENT_SHOW);
                return;
            }
            try {
                event = eventService.setEventCategory(event, callbackData);
                context.getHeaders().put("event", event);
            } catch (CategoryNotFoundException e) {
                SendMessage editMessage = new SendMessage();
//                editMessage.setMessageId(context.get("messageId"));
                editMessage.setChatId(chatId);
                editMessage.setText("Такой категории нет, выбери другую");
                bot.send(editMessage);
            }
            context.setNextState(LocalChatStates.EDIT_EVENT_TEMPLATE);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
