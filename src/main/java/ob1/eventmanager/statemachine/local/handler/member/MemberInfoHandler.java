package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.TemplateQuestionEntity;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.EventInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component("memberInfoHandler")
public class MemberInfoHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.WAIT_COMMANDS) {
            final EventEntity event = context.get("event");
            final String chatId = context.get("chatId");
            final EventInformation information = new EventInformation(event);
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setText(information.showAll());
            sendMessage.setChatId(chatId);
            bot.send(sendMessage);
            context.setNextState(LocalChatStates.MEMBER_INFO);
        } else if (previousState == LocalChatStates.MEMBER_INFO) {
            context.setNextState(LocalChatStates.MEMBER_PLACE);
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }
}
