package ob1.eventmanager.statemachine.event.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.exception.IncorrectDateFormatException;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;

public class EventPlaceHandler implements MessageStateMachineHandler<EventStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Override
    public void handle(MessageStateMachineContext<EventStates> context) {
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final EventEntity event = context.get("event");

        final EventStates previousState = context.getPreviousState();
        if (previousState == EventStates.DATE) {
            bot.send("Где будет проходить меропроиятие?", chatId);
        } else if (previousState == EventStates.PLACE) {
            eventService.setEventPlace(event, text);
            bot.send("Так и запишем: " + text, chatId);
            context.setNextState(EventStates.CATEGORY);
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
