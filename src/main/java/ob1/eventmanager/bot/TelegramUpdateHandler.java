package ob1.eventmanager.bot;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface TelegramUpdateHandler {

    void handle(Update update);

}
