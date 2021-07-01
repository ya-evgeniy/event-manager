package ob1.eventmanager.bot.command;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.UserService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;

@LocalCommand("new_event")
public class NewEventCommand implements LocalCommandHandler {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        if (stateMachine.getCurrentState() != LocalChatStates.WAIT_COMMANDS) {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId((String) headers.get("chatId"));
            sendMessage.setText("Ты сейчас заполняешь что-то другое, как закончишь, попробуй заново.");

            bot.send(sendMessage);
            return;
        }

        UserEntity userEntity = (UserEntity) headers.get("user");
        final EventEntity event = eventService.newEvent(
                userEntity,
                (long) headers.get("chatIdLong")
        );
        headers.put("event", event);

        userEntity = userService.setUserSelectedEvent(
                userEntity,
                event
        );
        headers.put("user", userEntity);

        stateMachine.setCurrentState(LocalChatStates.EVENT_CREATE);
        stateMachine.handle(headers);
    }

}
