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

@Component("localEventTimeHandler")
public class EventTimeHandler implements MessageStateMachineHandler<LocalChatStates> {

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
        if (previousState == LocalChatStates.EVENT_DATE) {
            bot.send("Напишите время мероприятия\nПример: 16:42 или 16.42 или 16 42", chatId);
        }
        else if (previousState == LocalChatStates.EVENT_TIME) {
            try {
                event = eventService.setEventTime(event, text);
                context.getHeaders().put("event", event);

                bot.send("Мероприятие пройдет в " + text, chatId);
                context.setNextState(LocalChatStates.EVENT_CATEGORY);
            } catch (IncorrectDateFormatException e) {
                bot.send("Некоректная дата", chatId);
            }
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
