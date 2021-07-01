package ob1.eventmanager.statemachine.local.handler.event;

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


@Component("localEventCategoryHandler")
public class EventCategoryHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EventService eventService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final String chatId = context.get("chatId");
        final EditMessageText editMessage = new EditMessageText();

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.EVENT_TIME) {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Выберите категорию, к которой относится ваше мероприятие:");
            sendMessage.setChatId(chatId);
            InlineKeyboardMarkup categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtons(categoryService.getCategories());
            sendMessage.setReplyMarkup(categoryKeyboardMarkup);
            bot.send(sendMessage);
        } else if (previousState == LocalChatStates.EVENT_CATEGORY) {
            final String callbackData = context.get("callbackData");
            try {
                event = eventService.setEventCategory(event, callbackData);
                context.getHeaders().put("event", event);
                context.setNextState(LocalChatStates.EVENT_TEMPLATE);
            } catch (CategoryNotFoundException e) {
                editMessage.setMessageId(context.get("messageId"));
                editMessage.setChatId(chatId);
                editMessage.setText("Такой категории нет, выбери другую");
                bot.send(editMessage);
            }

        } else if (previousState == LocalChatStates.EVENT_TEMPLATE) {
            editMessage.setText("Выберите категорию, к которой относится ваше мероприятие:");
            editMessage.setChatId(chatId);
            editMessage.setMessageId(context.get("messageId"));
            InlineKeyboardMarkup categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtons(categoryService.getCategories());
            editMessage.setReplyMarkup(categoryKeyboardMarkup);
            bot.send(editMessage);
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }
}
