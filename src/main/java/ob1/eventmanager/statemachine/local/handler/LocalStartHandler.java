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
            bot.send("Привет, меня зовут EVE! Я твой личный менеджер мероприятий.\n Мои основные задачи:\n " +
                            "- помогать организовывать мероприятие;\n" +
                            "- уведомлять участников о статуте мероприятия;\n" +
                            "- опрашивать участников мероприятия;\n" +
                            "- выводить статистику опросов;\n" +
                            "- составлять отчет по результатом опросов.\n" +
                            "\n" +
                            "Вот команды которыми ты можешь пользоваться:\n" +
                            "/new_event - Создать новое мероприятие\n" +
                            "/actual_events - Посмотреть актуальные мероприятия\n" +
                            "/manage_events - Управлять созданными мероприятиями",
                    chatId
            );
            context.setNextState(LocalChatStates.CHECK_ACTUAL_EVENTS);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
