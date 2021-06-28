package ob1.eventmanager.bot.command;

import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;

import java.util.Map;

public interface LocalCommandHandler {

    String getId();

    void handle(MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers);

}
