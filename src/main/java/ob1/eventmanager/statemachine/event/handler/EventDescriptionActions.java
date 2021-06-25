package ob1.eventmanager.statemachine.event.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.event.EventEvents;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.MessageHeaders;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class EventDescriptionActions {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    public void nameStateHandler(MessageStateMachineContext<EventStates> context) {
        final String text = context.get("text");
        final EventEntity event = context.get("event");
        final String chatId = context.get("chatId");

        final EventStates previousState = context.getPreviousState();
        if (previousState == EventStates.NEW) {
            bot.send("Что ж, приступим.\nВведите название вашего мероприятия.", chatId);
        }
        else if (previousState == EventStates.NAME) {
            eventService.setEventName(event, text);
            context.setNextState(EventStates.DATE);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> NAME");
        }
    }

    public void waitName(StateContext<EventStates, EventEvents> context) {
        bot.send("Что ж, приступим.\n" +
                "Введите название вашего мероприятия.",(String) context.getMessage().getHeaders().get("chatId"));
    }

    public void waitDate(StateContext<EventStates, EventEvents> context) {
        final MessageHeaders headers = context.getMessageHeaders();

        final String text = (String) headers.get("text");
        final String chatId = (String) headers.get("chatId");
        final EventEntity event = (EventEntity) headers.get("event");

        eventService.setEventName(event, text);

        bot.send(text + " - отличное название!", chatId);
        bot.send("Напишите дату и время мероприятия в формате hh.mm.yyyy hh:mm", chatId);
    }

    public void waitPlace(StateContext<EventStates, EventEvents> context) {
        final MessageHeaders headers = context.getMessageHeaders();

        final String text = (String) headers.get("text");
        final String chatId = (String) headers.get("chatId");
        final EventEntity event = (EventEntity) headers.get("event");

        eventService.setEventDate(event, text);

        bot.send("Предупредите людей, чтобы на: " + text + " ничего не планировали", chatId);
//            bot.send("Кажется, дата была введена неверно :( Попробуйте еще раз в формате 12/12/2012/12:30",(String)context.getMessage().getHeaders().get("chatId"));
        bot.send("Где будет проходить меропроиятие?", chatId);
    }

}
