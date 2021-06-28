package ob1.eventmanager.statemachine.local.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.exception.IncorrectDateFormatException;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("eventDateHandler")
public class EventDateHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        System.out.println(context.getPreviousState() + " -> " + context.getCurrentState());

        EventEntity event = context.get("event");
        final String text = context.get("text");
        final String chatId = context.get("chatId");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.EVENT_NAME) {
            bot.send("Напишите дату и время мероприятия в формате hh.mm.yyyy hh:mm", chatId);
        }
        else if (previousState == LocalChatStates.EVENT_DATE) {
            try {
                event = eventService.setEventDate(event, text);
                context.getHeaders().put("event", event);

                bot.send("Предупредите людей, чтобы на: " + text + " ничего не планировали", chatId);
                context.setNextState(LocalChatStates.EVENT_PLACE);
            } catch (IncorrectDateFormatException e) {
                bot.send("Некоректная дата", chatId);
            }
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
