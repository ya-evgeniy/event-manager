package ob1.eventmanager.statemachine.local.handler.edit;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.ObjectsToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component("localEditEventDateHandler")
public class EditEventDateHandler implements MessageStateMachineHandler<LocalChatStates> {

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
            editMessageText.enableHtml(true);
            editMessageText.setText(String.format("Введите новую дату для мероприятия" +
                    "\nПример: 25.05.2021 или 25 мая 2021 (год писать не обязательно, возьмется текущий)" +
                    "\n<b>Текущая дата:</b> %s", ObjectsToString.date(event.getDate())));
            bot.send(editMessageText);
        } else if (previousState == LocalChatStates.EDIT_EVENT_DATE) {

            event = eventService.setEventDate(event, text);
            context.getHeaders().put("event", event);

            bot.send("Новая дата для мероприятия: " + text, chatId);
            context.setNextState(LocalChatStates.EDIT_EVENT_TIME);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
