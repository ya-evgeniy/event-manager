package ob1.eventmanager.bot.handler;

import ob1.eventmanager.bot.TelegramUpdateHandler;
import ob1.eventmanager.service.MessageStateMachineService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;

@Component("telegramLocalMessageUpdateHandler")
public class LocalMessageUpdateHandler implements TelegramUpdateHandler {

    @Autowired
    private MessageStateMachineService stateMachineService;

    @Override
    public void handle(Update update) {
        final Message message = update.getMessage();
        final User user = message.getFrom();
        final Chat chat = message.getChat();
        final String text = message.getText();

        Map<String, Object> headers = new HashMap<>();
        headers.put("messageId", message.getMessageId());
        headers.put("userId", user.getId());
        headers.put("chatId", String.valueOf(chat.getId()));

        final MessageStateMachine<LocalChatStates> stateMachine = getOrCreateStateMachine(chat);

        if (text.startsWith("/")) {
            handleCommand(text.substring(1), stateMachine, headers);
            return;
        }

        stateMachine.handle(headers);
    }

    private void handleCommand(String command, MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        switch (command) {
            case "start":

                break;
            case "new_event":
                throw new UnsupportedOperationException();
            case "edit_event":
                throw new UnsupportedOperationException();
            case "edit_answers":
                throw new UnsupportedOperationException();
        }
    }

    private MessageStateMachine<LocalChatStates> getOrCreateStateMachine(Chat chat) {
        final long chatId = chat.getId();
        if (stateMachineService.hasLocal(chatId)) {
            return stateMachineService.getLocal(chatId);
        }
        else {
            return stateMachineService.createLocal(chat, LocalChatStates.NEW);
        }
    }

}
