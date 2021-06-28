package ob1.eventmanager.bot.handler;

import ob1.eventmanager.bot.TelegramUpdateHandler;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.springframework.stereotype.Component;

@Component("telegramGroupUpdateHandler")
public class GroupUpdateHandler implements TelegramUpdateHandler {

    @Override
    public void handle(Update update) {

        /*
            Если пригласили бота в чат:
                Узнать кто пригласил
                Получить мероприятие созданное пригласителем
                Отправить инфу о мероприятии
                Закрепить сообщение

            Если человек вошел в чат:
                Если имеется диалог:
                    Написать в ЛС
                    Переключить состояния
                Иначе:
                    Попросить его написать в ЛС

            Если человек вышел из чата:
                Написать организатору о ливе человека в ЛС

         */

    }

}
