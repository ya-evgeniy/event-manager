package ob1.eventmanager.statemachine.event.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.categoryChooser.CategoryButtonBuilder;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.exception.IncorrectDateFormatException;
import ob1.eventmanager.service.CategoryService;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Component
public class EventCategoryHandler implements MessageStateMachineHandler<EventStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private EventService eventService;

    @Override
    public void handle(MessageStateMachineContext<EventStates> context) {
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final EventEntity event = context.get("event");

        final EventStates previousState = context.getPreviousState();
        if (previousState == EventStates.PLACE) {
            bot.send("Так и запишем: " + text, chatId);
        }
        else if (previousState == EventStates.CATEGORY) {
            try {
                eventService.setEventPlace(event, text);
                final SendMessage sendMessage = new SendMessage();
                sendMessage.setText("Выберите категорию, к которой относится ваше мероприятие:");
                sendMessage.setChatId(chatId);
                InlineKeyboardMarkup categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtons(categoryService.getCategories());
                sendMessage.setReplyMarkup(categoryKeyboardMarkup);
                bot.send(sendMessage);
                context.setNextState(EventStates.TEMPLATE);
            } catch (IncorrectDateFormatException e) {
                bot.send("Выберите категорию", chatId);
            }
        }
        else if (previousState == EventStates.TEMPLATE) {
            eventService.setEventPlace(event, text);
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setText("Выберите категорию, к которой относится ваше мероприятие:");
            sendMessage.setChatId(chatId);
            InlineKeyboardMarkup categoryKeyboardMarkup = new CategoryButtonBuilder().buildCategoryButtons(categoryService.getCategories());
            sendMessage.setReplyMarkup(categoryKeyboardMarkup);
            bot.send(sendMessage);
            context.setNextState(EventStates.TEMPLATE);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }
}
