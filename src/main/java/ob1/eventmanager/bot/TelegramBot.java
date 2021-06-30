package ob1.eventmanager.bot;

import ob1.eventmanager.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.groupadministration.KickChatMember;
import org.telegram.telegrambots.meta.api.methods.groupadministration.LeaveChat;
import org.telegram.telegrambots.meta.api.methods.pinnedmessages.PinChatMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired @Qualifier("telegramGroupUpdateHandler")
    private TelegramUpdateHandler groupUpdateHandler;

    @Autowired @Qualifier("telegramLocalUpdateHandler")
    private TelegramUpdateHandler localMessageUpdateHandler;

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
            groupUpdateHandler.handle(update);
        }
        else {
            localMessageUpdateHandler.handle(update);
        }
    }

    public Optional<Integer> getSelfId() {
        try {
            return Optional.of(getMe().getId());
        } catch (TelegramApiException e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    public Message send(String text, String id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(id);
        return send(sendMessage);
    }

    public void edit(String text, String chatId, int messageId) {
        final EditMessageText editMessage = new EditMessageText();
        editMessage.setChatId(chatId);
        editMessage.setMessageId(messageId);
        editMessage.setText(text);
        send(editMessage);
    }

    public Message send(SendMessage message) {
        try {
            return execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
        return null;
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

    public void pinMessage(String chatId, Message message) {
        try {
            execute(new PinChatMessage(chatId, message.getMessageId()));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public boolean hasChat(UserEntity user) {
        if (user.getChatId() == null) return false;

        final GetChat getChat = new GetChat(String.valueOf(user.getChatId()));

        try {
            final Chat chat = execute(getChat);
            return true;
        } catch (TelegramApiException e) {
            return false;
        }
    }

    public void send(KickChatMember kick) {
        try {
            execute(kick);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public String getMarkdownMention(UserEntity user) {
        return String.format("[%s](tg://user?id=%s)", user.getName(), user.getTelegramId());
    }

}
