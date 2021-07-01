package ob1.eventmanager.statemachine.local.handler.edit;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionAnswerEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.repository.EventRepository;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.UserService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.KeyboardUtils;
import ob1.eventmanager.utils.ObjectsToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;

import static ob1.eventmanager.utils.KeyboardUtils.buttonOf;

@Component("localEditEventShowHandler")
public class EditEventShowHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private EventService eventService;

    @Autowired
    private UserService userService;

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        String chatId = context.get("chatId");
        UserEntity user = context.get("user");

        EventEntity event = context.get("event");
        event = eventService.getEventById(event.getId());
        context.getHeaders().put("event", event);

        final SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(ObjectsToString.eventForOwner(event));
        message.enableHtml(true);

        message.setReplyMarkup(KeyboardUtils.inlineOf(
                buttonOf("Изменить название", "/edit_event_name " + event.getId()),
                buttonOf("Изменить место", "/edit_event_place " + event.getId()),
                buttonOf("Изменить дату", "/edit_event_date " + event.getId()),
                buttonOf("Изменить время", "/edit_event_time " + event.getId()),
                buttonOf("Изменить вопросы", "/edit_event_questions " + event.getId()),
                buttonOf("Получить статистику", "/get_event_stats " + event.getId()),
                buttonOf("Отменить мероприятие", "/cancel_event " + event.getId()),
                buttonOf("Назад", "/manage_events")
        ));

        bot.send(message);

        userService.setUserSelectedEvent(user, null);
        context.setNextState(LocalChatStates.WAIT_COMMANDS);
    }

}
