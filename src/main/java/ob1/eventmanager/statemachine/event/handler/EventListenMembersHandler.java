package ob1.eventmanager.statemachine.event.handler;

import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.stereotype.Component;

@Component("eventListenMembersHandler")
public class EventListenMembersHandler implements MessageStateMachineHandler<EventStates> {

    @Override
    public void handle(MessageStateMachineContext<EventStates> context) {
        System.out.println(context.getPreviousState() + " -> " + context.getCurrentState());


    }

}
