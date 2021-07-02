package ob1.eventmanager.statemachine.local.handler.event;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.KeyboardUtils;
import ob1.eventmanager.utils.ObjectsToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import static ob1.eventmanager.utils.KeyboardUtils.buttonOf;

@Component("localEventConfirmHandler")
public class EventConfirmHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final String chatId = context.get("chatId");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.EVENT_TEMPLATE_QUESTION) {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(ObjectsToString.eventForOwner(event));
            sendMessage.enableHtml(true);

            sendMessage.setReplyMarkup(KeyboardUtils.inlineOf(
                    buttonOf("Изменить название", "/edit_event_name " + event.getId()),
                    buttonOf("Изменить место", "/edit_event_place " + event.getId()),
                    buttonOf("Изменить дату", "/edit_event_date " + event.getId()),
                    buttonOf("Изменить время", "/edit_event_time " + event.getId()),
                    buttonOf("Изменить вопросы", "/edit_event_questions " + event.getId()),
                    buttonOf("Получить краткую статистику", "/get_event_short_stats " + event.getId()),
                    buttonOf("Получить полную статистику", "/get_event_final_stats " + event.getId()),
                    buttonOf("Отменить мероприятие", "/cancel_event " + event.getId()),
                    buttonOf("Назад", "/manage_events")
            ));

            bot.send(sendMessage);
            context.setNextState(LocalChatStates.WAIT_COMMANDS);
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}