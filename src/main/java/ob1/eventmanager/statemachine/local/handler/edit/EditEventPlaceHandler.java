package ob1.eventmanager.statemachine.local.handler.edit;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component("localEditEventPlaceHandler")
public class EditEventPlaceHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final int messageId = context.get("messageId");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.WAIT_COMMANDS) {
            final EditMessageText editMessageText = new EditMessageText();
            editMessageText.setMessageId(messageId);
            editMessageText.setChatId(chatId);
            editMessageText.setText(String.format("Напишите новое место для мероприятия" +
                    "\nТекущее место: %s", event.getPlace()));
            bot.send(editMessageText);
        } else if (previousState == LocalChatStates.EDIT_EVENT_PLACE) {

            event = eventService.setEventPlace(event, text);
            context.getHeaders().put("event", event);

            bot.send("Новое место для мероприятия: " + text, chatId);
            context.setNextState(LocalChatStates.EDIT_EVENT_SHOW);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }
}
