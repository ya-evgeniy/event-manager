package ob1.eventmanager.statemachine.local.handler.event;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("localEventNameHandler")
public class EventNameHandler implements MessageStateMachineHandler<LocalChatStates> {

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
        if (previousState == LocalChatStates.EVENT_CREATE) {
            bot.send("Что ж, приступим.\nВведи название своего мероприятия.", chatId);
        }
        else if (previousState == LocalChatStates.EVENT_NAME) {
            event = eventService.setEventName(event, text);
            context.getHeaders().put("event", event);

            context.setNextState(LocalChatStates.EVENT_PLACE);
            bot.send(text + " - отличное название!", chatId);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
