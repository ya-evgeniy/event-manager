package ob1.eventmanager.statemachine.event.handler;

import ob1.eventmanager.statemachine.event.EventEvents;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class EventCreateActions {

    public void createCheck(StateContext<EventStates, EventEvents> context) {
        System.out.println("CREATE CHECK");
    }

    public void waitConfirm(StateContext<EventStates, EventEvents> context) {
        System.out.println("WAIT CONFIRM");
    }

    public void listenMembers(StateContext<EventStates, EventEvents> context) {
        System.out.println("LISTEN MEMBERS");
    }

    public void leaveFromChat(StateContext<EventStates, EventEvents> context) {
        System.out.println("LEAVE FROM CHAT");
    }

}
