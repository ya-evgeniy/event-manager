package ob1.eventmanager.bot;

import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.UserService;
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
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;


@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.username}")
    private String botUsername;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Autowired
    private StateMachineFactory<EventStates, EventEvents> stateMachineFactory;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    private final Map<String, StateMachine<EventStates, EventEvents>> eventStateMachines = new HashMap<>();

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

        if (update.hasMessage()) {
            handleMessage(update);
        }
        else if (update.hasCallbackQuery()) {
            handleCallbackQuery(update);
        }
    }

    private void handleMessage(Update update) {
        final Message message = update.getMessage();
        if (message.getFrom().getIsBot()) return;

        final UserEntity user = userService.getUserByTelegramId(message.getFrom().getId());
        final String stringChatId = String.valueOf(message.getChat().getId());

        Map<String, Object> headers = new HashMap<>();
        headers.put("text", message.getText());
        headers.put("chatId", stringChatId);
        headers.put("userId", message.getFrom().getId());
        headers.put("messageId", message.getMessageId());

        StateMachine<EventStates, EventEvents> eventStateMachine = eventStateMachines.get(stringChatId);
        if (eventStateMachine == null && message.getGroupchatCreated() != null && message.getGroupchatCreated()) {
            final EventEntity event = eventService.newEvent(user, message.getChatId());
            headers.put("event", event);

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
        if (message.getFrom().getId() != user.getTelegramId()) return;

        final EventEntity event = eventService.getEvent(message.getChatId());
        headers.put("event", event);

        if (eventStateMachine != null) {
            final State<EventStates, EventEvents> state = eventStateMachine.getState();
            final EventEvents stateEvent = state.getId().getEvent();
            if (stateEvent != null) {
                eventStateMachine.sendEvent(new GenericMessage<>(
                        stateEvent,
                        headers
                ));
            }
        }
    }

    private void handleCallbackQuery(Update update) {
        final CallbackQuery callbackQuery = update.getCallbackQuery();

        final UserEntity user = userService.getUserByTelegramId(callbackQuery.getFrom().getId());
        final String stringChatId = String.valueOf(callbackQuery.getMessage().getChatId());

        Map<String, Object> headers = new HashMap<>();
        headers.put("chatId", stringChatId);
        headers.put("userId", callbackQuery.getFrom().getId());
        headers.put("callbackData", callbackQuery.getData());
        headers.put("messageId", callbackQuery.getMessage().getMessageId());

        StateMachine<EventStates, EventEvents> eventStateMachine = eventStateMachines.get(stringChatId);
        if (eventStateMachine != null) {
            final State<EventStates, EventEvents> state = eventStateMachine.getState();
            final EventEvents stateEvent = state.getId().getEvent();
            if (stateEvent != null) {
                eventStateMachine.sendEvent(new GenericMessage<>(
                        stateEvent,
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
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void send(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public void send(EditMessageText message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}
