package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.service.EventService;
import ob1.eventmanager.service.MemberAnswerService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;

@Component("memberPlaceEditHandler")
public class MemberPlaceEditHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;
    @Autowired
    private MemberAnswerService memberAnswerService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {

        System.out.println(context.getPreviousState() + " -> " + context.getCurrentState());

        final String text = context.get("text");
        final String chatId = context.get("chatId");
        final MemberEntity member = new MemberEntity();//fixme добавить getMemberEntity()

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.MEMBER_PLACE_EDIT) {
            bot.send("Введите название места, которое вас устраивает", chatId);
            memberAnswerService.setAnswer(member, text);//TODO
            bot.send("Окей, ваше пожелание по месту " + text + " будет обязательно учтено организатором.", chatId);
            context.setNextState(LocalChatStates.MEMBER_DATE);

        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }

    }

}
