package ob1.eventmanager;

import ob1.eventmanager.command.CommandHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;


@Component
public class Bot extends TelegramLongPollingBot {
    private String currentChatID;
    private Consumer<? super String> consumer;

    @Autowired
    public Bot(CommandHandler handler){
        this.consumer = handler;
    }

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

    public String getCurrentChatID() {
        return currentChatID;
    }

    @Override
    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();
        this.currentChatID = String.valueOf(message.getChatId());
        receiveData(message.getText()).subscribe(consumer);
    }

    public Flux<String> receiveData(String s) {
        return Flux.fromArray(new String[]{s});
    }

}
