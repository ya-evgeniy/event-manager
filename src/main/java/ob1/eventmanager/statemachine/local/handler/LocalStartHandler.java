package ob1.eventmanager.statemachine.local.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("localStartHandler")
public class LocalStartHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        final String chatId = context.get("chatId");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == null) {
            bot.send("Привет! Я ваш личный менеджер мероприятий.\n Мои основные задачи:\n " +
                            "- хранить данные о вашем мероприятии;\n" +
                            "- собирать статистику о предпочтениях гостей;\n" +
                            "- напоминать о предстоящем событии\n" +
                            "- всегда держать в курсе текущего положения дел.\n" +
                            "\n" +
                            "Вот команды которыми ты можешь пользоваться:\n" +
                            "/new_event - Создать новое мероприятие\n" +
                            "/actual_events - Посмотреть актуальные мероприятия",
                    chatId
            );
            context.setNextState(LocalChatStates.CHECK_ACTUAL_EVENTS);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
