package ob1.eventmanager.bot.handler;

import ob1.eventmanager.bot.TelegramUpdateHandler;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Component;

@Component("telegramGroupCallbackQueryUpdateHandler")
public class GroupCallbackQueryUpdateHandler implements TelegramUpdateHandler {

    @Override
    public void handle(Update update) {

    }

}
