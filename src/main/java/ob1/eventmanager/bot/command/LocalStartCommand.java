package ob1.eventmanager.bot.command;

import ob1.eventmanager.service.EventService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component("localStartCommand")
public class LocalStartCommand implements LocalCommandHandler {

    @Autowired
    private EventService eventService;

    @Override
    public String getId() {
        return "start";
    }

    @Override
    public void handle(MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        final String userId = (String) headers.get("userId");

        // send hello message

    }

}
