package ob1.eventmanager.statemachine.event.action;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.statemachine.event.EventEvents;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class EventQuestionActions {
    @Autowired
    TelegramBot bot;

    private String eventLocation = "";

    public void waitCategory(StateContext<EventStates, EventEvents> context) {
        eventLocation =  (String)     context.getMessage().getHeaders().get("text");
        bot.send("Так и запишем: " + eventLocation,(String) context.getMessage().getHeaders().get("chatId"));
        bot.send("Выберите категорию, к которой относится ваше мероприятие.",(String) context.getMessage().getHeaders().get("chatId"));
    }

    public void categoryCheck(StateContext<EventStates, EventEvents> context) {
        System.out.println("CATEGORY CHECK");
    }

    public void waitTemplate(StateContext<EventStates, EventEvents> context) {
        System.out.println("WAIT TEMPLATE");
    }

    public void templateCheck(StateContext<EventStates, EventEvents> context) {
        System.out.println("TEMPLATE CHECK");
    }

    public void questionCheck(StateContext<EventStates, EventEvents> context) {
        System.out.println("QUESTION CHECK");
    }

    public void waitQuestionsConfirm(StateContext<EventStates, EventEvents> context) {
        System.out.println("WAIT QUESTION CONFIRM");
    }

}
