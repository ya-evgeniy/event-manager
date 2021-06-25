package ob1.eventmanager.statemachine.event.action;

import ob1.eventmanager.statemachine.event.EventEvents;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.statemachine.StateContext;
import org.springframework.stereotype.Component;

@Component
public class EventQuestionActions {

    public void waitCategory(StateContext<EventStates, EventEvents> context) {
        System.out.println("WAIT CATEGORY");
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
