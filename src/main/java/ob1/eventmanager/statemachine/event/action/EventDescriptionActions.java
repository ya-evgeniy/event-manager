package ob1.eventmanager.statemachine.event.action;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.statemachine.event.EventEvents;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class EventDescriptionActions {
    private String eventName = "";
    private Date dateTime;
    @Autowired
    private TelegramBot bot;

    public void waitName(StateContext<EventStates, EventEvents> context) {
        bot.send("Что ж, приступим.\n" +
                "Введите название вашего мероприятия.",(String) context.getMessage().getHeaders().get("chatId"));
    }

    public void waitDate(StateContext<EventStates, EventEvents> context) {
        eventName = (String)     context.getMessage().getHeaders().get("text");
        bot.send(eventName+" - отличное название!",(String) context.getMessage().getHeaders().get("chatId"));
        bot.send("Напишите дату и время мероприятия в формате 12/12/2012/12:30",(String)context.getMessage().getHeaders().get("chatId"));
    }

    public void waitPlace(StateContext<EventStates, EventEvents> context) {
        SimpleDateFormat inFormat = new SimpleDateFormat("dd/MM/yyyy/HH:mm");
        try {
            dateTime = inFormat.parse((String) context.getMessage().getHeaders().get("text"));
            bot.send("Предупредите людей, чтобы на: " + dateTime.toString()+" ничего не планировали",(String)context.getMessage().getHeaders().get("chatId"));
        } catch (java.text.ParseException e) {
            bot.send("Кажется, дата была введена неверно :( Попробуйте еще раз в формате 12/12/2012/12:30",(String)context.getMessage().getHeaders().get("chatId"));
        }
        bot.send("Где будет проходить меропроиятие?",(String)context.getMessage().getHeaders().get("chatId"));
    }

}
