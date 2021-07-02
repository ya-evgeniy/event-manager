package ob1.eventmanager.bot.command;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Map;

@LocalCommand("set_event_date")
public class SetEventDateCommand implements LocalCommandHandler {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        final String[] commandArgs = (String[]) headers.get("commandArgs");
        final UserEntity user = (UserEntity) headers.get("user");
        final String chatId = (String) headers.get("chatId");


        if (commandArgs.length == 0) {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            bot.send(sendMessage);
            return;
        }
    }

}
