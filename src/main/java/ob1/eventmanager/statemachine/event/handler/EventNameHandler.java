package ob1.eventmanager.statemachine.event.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("eventNameHandler")
public class EventNameHandler implements MessageStateMachineHandler<EventStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Override
    public void handle(MessageStateMachineContext<EventStates> context) {
        System.out.println(context.getPreviousState() + " -> " + context.getCurrentState());

        EventEntity event = context.get("event");
        final String text = context.get("text");
        final String chatId = context.get("chatId");

        final EventStates previousState = context.getPreviousState();
        if (previousState == EventStates.NEW) {
            bot.send("Что ж, приступим.\nВведите название вашего мероприятия.", chatId);
        }
        else if (previousState == EventStates.NAME) {
            event = eventService.setEventName(event, text);
            context.getHeaders().put("event", event);

            context.setNextState(EventStates.DATE);
            bot.send(text + " - отличное название!", chatId);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
