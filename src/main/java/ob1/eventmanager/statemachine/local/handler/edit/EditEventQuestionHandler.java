package ob1.eventmanager.statemachine.local.handler.edit;

import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.stereotype.Component;

@Component("localEditEventQuestionHandler")
public class EditEventQuestionHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {

    }

}
