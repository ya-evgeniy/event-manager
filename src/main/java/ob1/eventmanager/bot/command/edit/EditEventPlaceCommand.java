package ob1.eventmanager.bot.command.edit;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.bot.command.LocalCommandHandler;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@LocalCommand("edit_event_place")
public class EditEventPlaceCommand implements LocalCommandHandler {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        String chatId = (String) headers.get("chatId");

        if (stateMachine.getCurrentState() != LocalChatStates.WAIT_COMMANDS) {
            bot.send(
                    "Похоже, что ты сейчас заполняешь какую-то информацию. Как закончишь, выполни команду заного.",
                    chatId
            );
            return;
        }

        UserEntity user = (UserEntity) headers.get("user");
        long chatIdLong = (long) headers.get("chatIdLong");

    }

}
