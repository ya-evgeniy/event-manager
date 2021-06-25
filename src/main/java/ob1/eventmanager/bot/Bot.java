package ob1.eventmanager.bot;

import ob1.eventmanager.bot.event_create_statemachine.EventCreateSession;
import ob1.eventmanager.bot.event_create_statemachine.EventEvents;
import ob1.eventmanager.bot.event_create_statemachine.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.TreeMap;


@Component
public class Bot extends TelegramLongPollingBot {
    private TreeMap<String, EventCreateSession> sessions = new TreeMap<>();
    @Autowired
    private StateMachineFactory<EventStates, EventEvents> stateMachineFactory;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

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
        Message message = update.getMessage();
        String chatId = String.valueOf(message.getChatId());

        if (!sessions.containsKey(chatId)) {
            sessions.put(chatId, new EventCreateSession(chatId, stateMachineFactory.getStateMachine(), this));
        }
        sessions.get(chatId).receive(message);
    }

    public void send(String text, String id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(id);
        try {
            this.execute(sendMessage);
        } catch (TelegramApiException telegramApiException) {
            telegramApiException.printStackTrace();
        }
    }
}
