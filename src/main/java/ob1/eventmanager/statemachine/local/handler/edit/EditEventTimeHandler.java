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

@Component("localEditEventTimeHandler")
public class EditEventTimeHandler implements MessageStateMachineHandler<LocalChatStates> {

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
            sendMessage.setText(String.format("Введи новое время для мероприятия" +
                    "\nПример: 16:42" +
                    "\n<b>Текущее время:</b> %s", ObjectsToString.time(event.getTime())));
            bot.send(sendMessage);
        } else if (previousState == LocalChatStates.EDIT_EVENT_TIME) {
            if (text == null) {
                bot.send("Напиши ответ текстом", chatId);
                return;
            }

            event = eventService.setEventTime(event, text);
            context.getHeaders().put("event", event);

            bot.send("Новое время для мероприятия: " + text, chatId);
            context.setNextState(LocalChatStates.EDIT_EVENT_SHOW);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
