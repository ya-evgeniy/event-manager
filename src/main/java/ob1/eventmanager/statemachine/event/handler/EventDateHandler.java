package ob1.eventmanager.statemachine.event.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.exception.IncorrectDateFormatException;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("eventDateHandler")
public class EventDateHandler implements MessageStateMachineHandler<EventStates> {

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
        if (previousState == EventStates.NAME) {
            bot.send("Напишите дату и время мероприятия в формате hh.mm.yyyy hh:mm", chatId);
        }
        else if (previousState == EventStates.DATE) {
            try {
                event = eventService.setEventDate(event, text);
                context.getHeaders().put("event", event);

                bot.send("Предупредите людей, чтобы на: " + text + " ничего не планировали", chatId);
                context.setNextState(EventStates.PLACE);
            } catch (IncorrectDateFormatException e) {
                bot.send("Некоректная дата", chatId);
            }
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
