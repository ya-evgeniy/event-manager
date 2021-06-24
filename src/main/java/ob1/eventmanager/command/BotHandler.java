package ob1.eventmanager.command;

import ob1.eventmanager.Bot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public abstract class BotHandler {
    public Bot bot;

    public void setBot(Bot bot) {
        this.bot = bot;
    }

    public Bot getBot() {
        return bot;
    }

    public void send(String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(bot.getCurrentChatID());
        try {
            bot.execute(sendMessage);
        } catch (TelegramApiException telegramApiException) {
            telegramApiException.printStackTrace();
        }
    }
}
