package ob1.eventmanager.statemachine.local.handler;

import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.stereotype.Component;

@Component("localWaitCommandsHandler")
public class WaitCommandsHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {

    }

}
