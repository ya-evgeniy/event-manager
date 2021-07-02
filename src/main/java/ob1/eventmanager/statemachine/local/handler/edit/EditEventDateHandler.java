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
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
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
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.enableHtml(true);
            sendMessage.setText(String.format("Введи новую дату для мероприятия" +
                    "\nПример: 25.05.2021 или 25 мая 2021 (год писать не обязательно, по умолчанию используется текущий год)" +
                    "\n<b>Текущая дата:</b> %s", ObjectsToString.date(event.getDate())));
            bot.send(sendMessage);
        } else if (previousState == LocalChatStates.EDIT_EVENT_DATE) {
            if (text == null) {
                bot.send("Напиши ответ текстом", chatId);
                return;
            }

            event = eventService.setEventDate(event, text);
            context.getHeaders().put("event", event);

            bot.send("Новая дата для мероприятия: " + text, chatId);
            context.setNextState(LocalChatStates.EDIT_EVENT_SHOW);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
