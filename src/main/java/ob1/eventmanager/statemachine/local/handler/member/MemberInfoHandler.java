package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.EventQuestionEntity;
import ob1.eventmanager.entity.TemplateQuestionEntity;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.EventInformation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component("localMemberInfoHandler")
public class MemberInfoHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        final LocalChatStates previousState = context.getPreviousState();
        if (previousState != LocalChatStates.WAIT_COMMANDS) {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }

        final EventEntity event = context.get("event");
        final String chatId = context.get("chatId");

        final StringBuilder builder = new StringBuilder();
        builder.append("Название: ").append(event.getName()).append("\n");
        builder.append("Место: ").append(event.getPlace()).append("\n");
        builder.append("Время: ").append(event.getDate()).append("\n\n");
        builder.append("Вопросы: ");

        for (EventQuestionEntity question : event.getQuestions()) {
            builder.append("\n").append(question.getQuestion());
        }

        final SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(builder.toString());
        bot.send(sendMessage);

        context.setNextState(LocalChatStates.MEMBER_PLACE);
    }
}
