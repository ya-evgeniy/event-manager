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
        final SendMessage sendMessage1 = new SendMessage();
        final String callbackData = context.get("callbackData");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.EVENT_TIME) {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Выбери категорию, к которой относится ваше мероприятие:");
            sendMessage.setChatId(chatId);
            InlineKeyboardMarkup categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtons(categoryService.getCategories());
            sendMessage.setReplyMarkup(categoryKeyboardMarkup);
            bot.send(sendMessage);
        } else if (previousState == LocalChatStates.EVENT_CATEGORY) {
            if (callbackData == null) {
                bot.send("Выбери из того что есть", chatId);
                return;
            }
            try {
                event = eventService.setEventCategory(event, callbackData);
                context.getHeaders().put("event", event);
                context.setNextState(LocalChatStates.EVENT_TEMPLATE);
            } catch (CategoryNotFoundException e) {
                sendMessage1.setChatId(chatId);
                sendMessage1.setText("Такой категории нет, выбери другую");
                bot.send(sendMessage1);
            }

        } else if (previousState == LocalChatStates.EVENT_TEMPLATE) {
            sendMessage1.setText("Выбери категорию, к которой относится ваше мероприятие:");
            sendMessage1.setChatId(chatId);
            InlineKeyboardMarkup categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtons(categoryService.getCategories());
            sendMessage1.setReplyMarkup(categoryKeyboardMarkup);
            bot.send(sendMessage1);
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }
}
