package ob1.eventmanager.bot.command;

import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;

import java.util.Map;

@LocalCommand("actual_events")
public class ActualEventsCommand implements LocalCommandHandler {

    @Override
    public void handle(MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        if (stateMachine.getCurrentState() != LocalChatStates.WAIT_COMMANDS) {
            return;
        }
        stateMachine.setCurrentState(LocalChatStates.CHECK_ACTUAL_EVENTS);
        stateMachine.handle(headers);
    }

}
