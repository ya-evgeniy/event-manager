package ob1.eventmanager.statemachine.local.handler.event;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.exception.IncorrectDateFormatException;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("localEventDateHandler")
public class EventDateHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final String text = context.get("text");
        final String chatId = context.get("chatId");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.EVENT_PLACE) {
            bot.send("Напишите дату мероприятия" +
                    "\nПример: 25.05.2021 или 25 мая 2021 (год писать не обязательно, возьмется текущий)", chatId);
        }
        else if (previousState == LocalChatStates.EVENT_DATE) {
            try {
                event = eventService.setEventDate(event, text);
                context.getHeaders().put("event", event);

                bot.send("Дата мероприятия: " + text, chatId);
                context.setNextState(LocalChatStates.EVENT_TIME);
            } catch (IncorrectDateFormatException e) {
                bot.send("Некоректная дата", chatId);
            }
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
