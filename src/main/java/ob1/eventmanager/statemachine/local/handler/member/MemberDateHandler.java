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

@Component("memberDateHandler")
public class MemberDateHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final Integer messageId = context.get("messageId");
        final String chatId = context.get("chatId");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.MEMBER_PLACE) {
            final EditMessageText editMessageText = new EditMessageText();

            editMessageText.setReplyMarkup(KeyboardUtils.inlineOf(
                    KeyboardUtils.buttonOf("Устраивает", "success"),
                    KeyboardUtils.buttonOf("Не устраивает", "cancel")
            ));

            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(messageId);
            editMessageText.setText(String.format("Мероприятие начнется %s", event.getDate()));

            bot.send(editMessageText);
        } else if (previousState == LocalChatStates.MEMBER_DATE) {
            final String callbackData = context.get("callbackData");
            if (callbackData.equals("cancel")) {
                bot.send("Напишите дату и время мероприятия в формате hh.mm.yyyy hh:mm", chatId);
                //TODO save new date of member (create new state)
            } else if (callbackData.equals("success")) {
                context.setNextState(LocalChatStates.MEMBER_QUESTION);
            } else {
                throw new UnsupportedOperationException(callbackData);
            }
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }
}
