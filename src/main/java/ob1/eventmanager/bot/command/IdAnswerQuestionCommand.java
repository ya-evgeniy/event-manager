package ob1.eventmanager.bot.command;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.UserService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

@LocalCommand("answer_questions")
public class IdAnswerQuestionCommand implements LocalCommandHandler {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Override
    public void handle(MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        String chatId = (String) headers.get("chatId");
        if (stateMachine.getCurrentState() != LocalChatStates.WAIT_COMMANDS) {
            bot.send("Ты сейчас заполняешь что-то другое, заполни и попробуй заново", chatId);
            return;
        }
        final String[] commandArgs = (String[]) headers.get("commandArgs");
        if (commandArgs.length < 1) {
            bot.send("Что-то тут не так, попробуй заново", chatId);
            return;
        }
        long eventId;
        try {
            eventId = Long.parseLong(commandArgs[0]);
        } catch (NumberFormatException e) {
            bot.send("Попробуй отправить число", chatId);
            return;
        }
        final EventEntity event = eventService.getEventById(eventId);
        UserEntity user = (UserEntity) headers.get("user");
        if (event == null) {
            bot.send("Что-то пошло не так, попробуй еще раз", chatId);
            return;
        }
        UserEntity finalUser = user;
        boolean isMember = event.getMembers().stream()
                .map(MemberEntity::getUser)
                .map(UserEntity::getId)
                .anyMatch(id -> id == finalUser.getId());
        if (!isMember) {
            bot.send("Кажется ты не участвуешь в мероприятии", chatId);
            return;
        }
        user = userService.setUserSelectedEvent(user, event);
        headers.put("user", user);
        stateMachine.setCurrentState(LocalChatStates.MEMBER_INFO);
        stateMachine.handle(headers);
    }
}
