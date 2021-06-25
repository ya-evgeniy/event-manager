package ob1.eventmanager.bot.event_create_statemachine;

import org.springframework.statemachine.annotation.OnTransition;
import org.springframework.statemachine.annotation.WithStateMachine;

@WithStateMachine
public class EventCreateStateMachineLogic {

    @OnTransition(source="START", target = "EVENT_NAME")
    public void start(){
        System.out.println("works");
    }

}
