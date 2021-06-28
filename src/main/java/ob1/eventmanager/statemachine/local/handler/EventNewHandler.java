package ob1.eventmanager.statemachine.local.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("eventNewHandler")
public class EventNewHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        System.out.println(context.getPreviousState() + " -> " + context.getCurrentState());

        final String chatId = context.get("chatId");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == null) {
            bot.send("Привет! Я ваш личный менеджер мероприятий.\n Мои основные задачи:\n " +
                            "- хранить данные о вашем мероприятии;\n" +
                            "- собирать статистику о предпочтениях гостей;\n" +
                            "- напоминать о предстоящем событии\n" +
                            "- всегда держать в курсе текущего положения дел.\n" +
                            "Для того, чтобы ввести данные о своем мероприятии, воспользуйтесь командой /create",
                    chatId
            );
            context.setNextState(LocalChatStates.NAME);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
