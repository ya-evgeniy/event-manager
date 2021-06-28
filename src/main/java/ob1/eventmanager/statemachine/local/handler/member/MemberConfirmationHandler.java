package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateQuestionEntity;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.EventInformation;
import ob1.eventmanager.utils.KeyboardUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component("memberConfirmationHandler")
public class MemberConfirmationHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        EventEntity event = context.get("event");
        final String chatId = context.get("chatId");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.MEMBER_QUESTION) {
            final Integer messageId = context.get("messageId");
            final EditMessageText editMessageText = new EditMessageText();
            final EventInformation information = new EventInformation(event);
            editMessageText.setChatId(chatId);
            editMessageText.setMessageId(messageId);
            editMessageText.setText(information.showAll());
            editMessageText.setReplyMarkup(KeyboardUtils.inlineOf(
                    KeyboardUtils.buttonOf("Участвую", "success"),
                    KeyboardUtils.buttonOf("Не участвую", "cancel"),
                    KeyboardUtils.buttonOf("Подумаю", "think"),
                    KeyboardUtils.buttonOf("Изменить", "edit")
            ));

            bot.send(editMessageText);
        } else if (previousState == LocalChatStates.MEMBER_CONFIRM) {
            final Integer messageId = context.get("messageId");
            final String callbackData = context.get("callbackData");
            if (callbackData.equals("cancel")) {
                final EditMessageText editMessageText = new EditMessageText();
                editMessageText.setChatId(chatId);
                editMessageText.setMessageId(messageId);
                editMessageText.setText("Вы отказались от участия");
                bot.send(editMessageText);
                //TODO close event
            } else if (callbackData.equals("success")) {
                final EventInformation information = new EventInformation(event);
                final EditMessageText editMessageText = new EditMessageText();
                editMessageText.enableHtml(true);
                editMessageText.setText(String.format(
                        "<b>Мероприятие подтверждено!</b>\n%s", information.showAll()));
                editMessageText.setMessageId(messageId);
                editMessageText.setChatId(chatId);
                bot.send(editMessageText);
            } else if (callbackData.equals("edit")) {
                //TODO edit date place or question
            } else if (callbackData.equals("think")) {
                //TODO if member want to think
            }
            else {
                throw new UnsupportedOperationException(callbackData);
            }
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }
}
