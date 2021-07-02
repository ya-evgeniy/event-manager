package ob1.eventmanager.statemachine.local.handler.event;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("localEventCreateHandler")
public class EventCreateHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        final String chatId = context.get("chatId");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.WAIT_COMMANDS) {
            bot.send("О! Неужели есть идея для мероприятия? Я с радостью помогу с его организацией :)", chatId);
            context.setNextState(LocalChatStates.EVENT_NAME);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
