package ob1.eventmanager.bot;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.UserService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.MessageStateMachineFactory;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    private MessageStateMachineFactory<LocalChatStates> eventStatesMachineFactory;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired @Qualifier("telegramGroupCallbackQueryUpdateHandler")
    private TelegramUpdateHandler groupCallbackQueryUpdateHandler;

    @Autowired @Qualifier("telegramGroupMessageUpdateHandler")
    private TelegramUpdateHandler groupMessageUpdateHandler;

    @Autowired @Qualifier("telegramLocalCallbackQueryUpdateHandler")
    private TelegramUpdateHandler localCallbackQueryUpdateHandler;

    @Autowired @Qualifier("telegramLocalMessageUpdateHandler")
    private TelegramUpdateHandler localMessageUpdateHandler;

    private final Map<String, MessageStateMachine<LocalChatStates>> eventStateMachines = new HashMap<>();

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);

        boolean isGroup = update.hasMessage() && update.getMessage().getChat().isGroupChat()
                || update.hasCallbackQuery() && update.getCallbackQuery().getMessage().getChat().isGroupChat();

        if (isGroup) {
            if (update.hasMessage()) {
                groupMessageUpdateHandler.handle(update);
            }
            else if (update.hasCallbackQuery()) {
                groupCallbackQueryUpdateHandler.handle(update);
            }
        }
        else {
            if (update.hasMessage()) {
                localMessageUpdateHandler.handle(update);
            }
            else if (update.hasCallbackQuery()) {
                localCallbackQueryUpdateHandler.handle(update);
            }
        }
    }

    private void handleLocalMessage(Update update) {
        final Message message = update.getMessage();
        if (message.getFrom().getIsBot()) return;

        final UserEntity user = userService.getUserByTelegramId(message.getFrom().getId());
        final String stringChatId = String.valueOf(message.getChat().getId());

        Map<String, Object> headers = new HashMap<>();
        headers.put("text", message.getText());
        headers.put("chatId", stringChatId);
        headers.put("userId", message.getFrom().getId());
        headers.put("messageId", message.getMessageId());

        MessageStateMachine<LocalChatStates> machine = eventStateMachines.get(stringChatId);
        if (machine == null && message.getGroupchatCreated() != null && message.getGroupchatCreated()) {
            final EventEntity event = eventService.newEvent(user, message.getChatId());
            headers.put("event", event);

            machine = eventStatesMachineFactory.create(stringChatId, LocalChatStates.NEW);
            eventStateMachines.put(stringChatId, machine);

            machine.handle(headers);
            return;
        }

        if (message.getText() == null || message.getText().isBlank()) return;
        if (message.getFrom().getId() != user.getTelegramId()) return;

        final EventEntity event = eventService.getEvent(message.getChatId());
        headers.put("event", event);

        if (machine != null) {
            machine.handle(headers);
        }
    }

    private void handleLocalCallbackQuery(Update update) {
        final CallbackQuery callbackQuery = update.getCallbackQuery();

        final UserEntity user = userService.getUserByTelegramId(callbackQuery.getFrom().getId());
        final String stringChatId = String.valueOf(callbackQuery.getMessage().getChatId());

        Map<String, Object> headers = new HashMap<>();
        headers.put("chatId", stringChatId);
        headers.put("userId", callbackQuery.getFrom().getId());
        headers.put("callbackData", callbackQuery.getData());
        headers.put("messageId", callbackQuery.getMessage().getMessageId());

        final EventEntity event = eventService.getEvent(callbackQuery.getMessage().getChatId());
        headers.put("event", event);

        MessageStateMachine<LocalChatStates> machine = eventStateMachines.get(stringChatId);
        if (machine != null) {
            machine.handle(headers);
        }
    }

    public void send(String text, String id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(id);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void send(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void leaveFromChat(String chatId) {
        try {
            execute(new LeaveChat(chatId));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}
