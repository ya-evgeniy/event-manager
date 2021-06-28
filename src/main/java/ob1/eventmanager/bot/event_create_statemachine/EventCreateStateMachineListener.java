package ob1.eventmanager.bot.event_create_statemachine;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.statemachine.event.EventEvents;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.Date;

//@Component
//@Scope("prototype")
public class EventCreateStateMachineListener extends StateMachineListenerAdapter {
    private String eventName = "";
    private String eventLocation = "";
    private Date dateTime;
    private StateMachine<EventStates, EventEvents> stateMachine;

    private TelegramBot bot;

    private String currentState = "NONE";
    private String chatId;

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public void setStateMachine(StateMachine<EventStates, EventEvents> stateMachine) {
        this.stateMachine = stateMachine;
    }

    public void setBot(TelegramBot bot) {
        this.bot = bot;
    }

    @Override
    public void stateChanged(State from, State to) {
        this.currentState = to.getId().toString();
        switch (currentState) {
            case "EVENT_NAME":
                bot.send("Что ж, приступим.\n" +
                        "Введите название вашего мероприятия.",chatId);
                break;
            case "EVENT_LOCATION":
                bot.send("Где будет проходить меропроиятие?",chatId);
                break;
            case "EVENT_DATE":
                bot.send("Напишите дату и время мероприятия в формате 12/12/2012/12:30",chatId);
                break;
            case "FINISH":
                bot.send("Обращайтесь еще! Всегда рад помочь.",chatId);;
                break;
            default:
                bot.send("Болтаете, болтаете, и все не понимаю я вас :(",chatId);
                break;
        }
    }

  /*  public void receive(Message message) {
        this.chatId =  String.valueOf(message.getChatId());
        switch (currentState) {
            case "EVENT_NAME":
                eventName = message.getText();
                bot.send(eventName+" - отличное название!",chatId);
                stateMachine.sendEvent(EventEvents.EVENT_NAME_TYPED);
                break;
            case "EVENT_LOCATION":
                eventLocation = message.getText();
                bot.send("Так и запишем: " + eventLocation,chatId);
                stateMachine.sendEvent(EventEvents.EVENT_LOCATION_TYPED);
                break;
            case "EVENT_DATE":
                SimpleDateFormat inFormat = new SimpleDateFormat("dd/MM/yyyy/HH:mm");
                try {
                    dateTime = inFormat.parse(message.getText());
                    bot.send("Предупредите людей, чтобы на: " + dateTime.toString()+" ничего не планировали",chatId);
                    stateMachine.sendEvent(EventEvents.EVENT_DATE_TYPED);
                } catch (java.text.ParseException e) {
                    bot.send("Кажется, дата была введена неверно :( Попробуйте еще раз в формате 12/12/2012/12:30",chatId);
                }
                break;
            case "FINISH":
                bot.send("Обращайтесь еще! Всегда рад помочь.",chatId);
                stateMachine.sendEvent(EventEvents.FINISHED);
                break;
            default:
                bot.send("Болтаете, болтаете, и все не понимаю я вас :(",chatId);
                break;
        }
    }*/
}