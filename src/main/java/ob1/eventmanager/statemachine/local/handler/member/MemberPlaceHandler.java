package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.KeyboardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

import java.util.Objects;

import static ob1.eventmanager.utils.KeyboardUtils.buttonOf;

@Component("localMemberPlaceHandler")
public class MemberPlaceHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        final String chatId = context.get("chatId");
        final int messageId = context.get("messageId");
        final String callbackQuery = context.get("callbackData");
        final EventEntity event = context.get("event");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.MEMBER_INFO) {

            final EditMessageText editMessage = new EditMessageText();
            editMessage.setChatId(chatId);
            editMessage.setMessageId(messageId);
            editMessage.setText("Мероприятие будет проходить здесь: " + event.getPlace() + ". Устраивает ли тебя место проведения?");
            editMessage.setReplyMarkup(KeyboardUtils.inlineOf(
                    buttonOf("Устраивает", "confirm"),
                    buttonOf("Не устраивает", "cancel")
            ));
            bot.send(editMessage);

        } else if (previousState == LocalChatStates.MEMBER_PLACE) {
            if (Objects.equals(callbackQuery, "confirm")) {
                context.setNextState(LocalChatStates.MEMBER_DATE);
            }
            else if (Objects.equals(callbackQuery, "cancel")) {
                context.setNextState(LocalChatStates.MEMBER_PLACE_EDIT);
            }
            else {
                throw new UnsupportedOperationException(callbackQuery);
            }
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
