package ob1.eventmanager.bot;

import ob1.eventmanager.statemachine.event.EventEvents;
import ob1.eventmanager.statemachine.event.EventStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.state.State;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    private final Map<String, StateMachine<EventStates, EventEvents>> eventStateMachines = new HashMap<>();

    @Autowired
    private StateMachineFactory<EventStates, EventEvents> stateMachineFactory;

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Override
    public String getBotUsername() {
        return botUsername;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }

    @Override
    public void onUpdateReceived(Update update) {
        System.out.println(update);
        final Message message = update.getMessage();
        if (message == null) return;

        final String stringChatId = String.valueOf(message.getChat().getId());

        Map<String, Object> headers = new HashMap<>();
        headers.put("text", message.getText());
        headers.put("chatId", stringChatId);
        headers.put("userId", message.getFrom().getId());

        StateMachine<EventStates, EventEvents> eventStateMachine = eventStateMachines.get(stringChatId);
        if (eventStateMachine == null && message.getGroupchatCreated() != null && message.getGroupchatCreated()) {
            eventStateMachine = stateMachineFactory.getStateMachine(stringChatId);
            eventStateMachine.start();

            eventStateMachines.put(stringChatId, eventStateMachine);

            send("Привет! Я ваш личный менеджер мероприятий.\n Мои основные задачи:\n " +
                     "- хранить данные о вашем мероприятии;\n" +
                     "- собирать статистику о предпочтениях гостей;\n" +
                     "- напоминать о предстоящем событии\n" +
                     "- всегда держать в курсе текущего положения дел.\n" +
                     "Для того, чтобы ввести данные о своем мероприятии, воспользуйтесь командой /create",
                    stringChatId
            );

            eventStateMachine.sendEvent(new GenericMessage<>(
                    EventEvents.STARTED,
                    headers
            ));
            return;
        }

        if (message.getText() == null || message.getText().isBlank()) return;

        if (eventStateMachine != null) {
            final State<EventStates, EventEvents> state = eventStateMachine.getState();
            final EventEvents event = state.getId().getEvent();
            if (event != null) {
                eventStateMachine.sendEvent(new GenericMessage<>(
                        event,
                        headers
                ));
            }
        }
    }

    public void send(String text, String id) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(id);
        try {
            this.execute(sendMessage);
        } catch (TelegramApiException telegramApiException) {
            telegramApiException.printStackTrace();
        }
    }
}
