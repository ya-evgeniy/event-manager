package ob1.eventmanager.command;

import ob1.eventmanager.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.function.Consumer;

@Component
public class EventCreator extends BotHandler implements Runnable, Consumer<String> {
    private String eventName = "";
    private String eventLocation = "";
    private Date dateTime;
    private boolean isWorking = true;

    @Autowired
    public void setNewBot(Bot bot) {
        setBot(bot);
    }

    public boolean isWorking() {
        return isWorking;
    }

    @Override
    public void accept(String s) {
        if (eventName.isEmpty()) {
            eventName = s;
            send(eventName+" - отличное название!");
            send("Где будет проходить меропроиятие?");
        } else if (!eventName.isEmpty()&&eventLocation.isEmpty()) {
            eventLocation = s;
            send("Так и запишем: " + eventLocation);
            send("Напишите дату и время мероприятия в формате 12/12/2012/12:30");
        }else {
            SimpleDateFormat inFormat = new SimpleDateFormat("dd/MM/yyyy/HH:mm");
            try {
                dateTime = inFormat.parse(s);
                send("Предупредите людей, чтобы на: " + dateTime.toString()+" ничего не планировали");
            } catch (ParseException e) {
                send("Кажется, дата была введена неверно :( Попробуйте еще раз в формате 12/12/2012/12:30");
            }
        }

        if(!eventName.isEmpty()&&!eventLocation.isEmpty()&&(dateTime!=null)){isWorking = false;}

    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            if (!isWorking) {
                Thread.currentThread().interrupt();
            }
        }
    }
}
