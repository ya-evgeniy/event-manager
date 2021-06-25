package ob1.eventmanager.statemachine.event.action;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.statemachine.event.EventEvents;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class EventDescriptionActions {

    @Autowired
    private TelegramBot bot;

    public void waitName(StateContext<EventStates, EventEvents> context) {
        System.out.println("WAIT NAME");
    }

    public void waitDate(StateContext<EventStates, EventEvents> context) {
        System.out.println("WAIT DATE");
    }

    public void waitPlace(StateContext<EventStates, EventEvents> context) {
        System.out.println("WAIT PLACE");
    }

}
