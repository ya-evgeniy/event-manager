package ob1.eventmanager.bot.event_create_statemachine;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.statemachine.local.EventEvents;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.statemachine.StateMachine;
import org.telegram.telegrambots.meta.api.objects.Message;


public class EventCreateSession {
//    private final EventCreateStateMachineListener listener;
    private final TelegramBot bot;

    private static String START_COMMAND = "/start";
    private static String CREATE_COMMAND = "/create";

    private final StateMachine<LocalChatStates, EventEvents> stateMachine;

    private final String chatId;

    public EventCreateSession(String id, StateMachine<LocalChatStates, EventEvents> stateMachine, TelegramBot bot) {
        this.chatId = id;
        this.stateMachine = stateMachine;
        this.bot = bot;
//        this.listener = EventCreateStateMachineListenerFactory.create();
//        this.listener.setStateMachine(stateMachine);
//        this.listener.setBot(bot);
//        this.listener.setChatId(chatId);
//        stateMachine.addStateListener(listener);

    }

    public void receive(Message message) {
        String text = message.getText();
        if (text.startsWith(START_COMMAND)) {
            bot.send("Привет! Я ваш личный менеджер мероприятий.\n Мои основные задачи:\n " +
                    "- хранить данные о вашем мероприятии;\n" +
                    "- собирать статистику о предпочтениях гостей;\n" +
                    "- напоминать о предстоящем событии\n" +
                    "- всегда держать в курсе текущего положения дел.\n" +
                    "Для того, чтобы ввести данные о своем мероприятии, воспользуйтесь командой /create", chatId);
        } else if (text.startsWith(CREATE_COMMAND)) {
            stateMachine.sendEvent(EventEvents.STARTED);
            System.out.println(stateMachine.getState().toString());
        } else {
//            listener.receive(message);
        }
    }

}


