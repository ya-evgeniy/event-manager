package ob1.eventmanager.statemachine.local.handler.member;

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

import java.util.Objects;

@Component("localMemberTimeHandler")
public class MemberTimeHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final Integer messageId = context.get("messageId");
        final String chatId = context.get("chatId");
        final String callbackData = context.get("callbackData");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.MEMBER_DATE || previousState == LocalChatStates.MEMBER_DATE_EDIT) {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            sendMessage.setText(String.format("Мероприятие начнется в %s", ObjectsToString.time(event.getTime())));

            sendMessage.setReplyMarkup(KeyboardUtils.inlineOf(
                    KeyboardUtils.buttonOf("Устраивает", "success"),
                    KeyboardUtils.buttonOf("Не устраивает", "cancel")
            ));

            bot.send(sendMessage);
        }
        else if (previousState == LocalChatStates.MEMBER_TIME) {
            if (callbackData == null) {
                bot.send("Выбери из того что есть", chatId);
                return;
            }
            if (Objects.equals(callbackData, "success")) {
                context.setNextState(LocalChatStates.MEMBER_QUESTION);
            }
            else if (Objects.equals(callbackData, "cancel")) {
                context.setNextState(LocalChatStates.MEMBER_TIME_EDIT);
            }
            else {
                throw new UnsupportedOperationException(callbackData);
            }
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }
}
