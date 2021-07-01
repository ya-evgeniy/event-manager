package ob1.eventmanager.bot.command.edit;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.bot.command.LocalCommandHandler;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.exception.EventNotFoundException;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.UserService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

@LocalCommand("edit_event_time")
public class EditEventTimeCommand implements LocalCommandHandler {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        String chatId = (String) headers.get("chatId");

        if (stateMachine.getCurrentState() != LocalChatStates.WAIT_COMMANDS) {
            bot.send(
                    "Похоже, что ты сейчас заполняешь какую-то информацию. Как закончишь, выполни команду заного.",
                    chatId
            );
            return;
        }

        UserEntity user = (UserEntity) headers.get("user");

        final String[] commandArgs = (String[]) headers.get("commandArgs");

        if (commandArgs.length == 0) {
            bot.send("Выбранное мероприятие не найдено, попробуй выбрать другое.", chatId);
            return;
        }

        EventEntity event;
        try {
            long eventId = Long.parseLong(commandArgs[0]);
            event = eventService.getEventById(eventId);
        }
        catch (NumberFormatException | EventNotFoundException e) {
            bot.send("Выбранное мероприятие не найдено, попробуй выбрать другое.", chatId);
            return;
        }

        if (event.getOwner().getId() != user.getId()) {
            bot.send("Мероприятие не пренадлежит тебе, ты не можешь его изменять!", chatId);
            return;
        }

        userService.setUserSelectedEvent(user, event);
        headers.put("event", event);
        stateMachine.setCurrentState(LocalChatStates.EDIT_EVENT_TIME);
        stateMachine.handle(headers);
    }

}
