package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("localMemberTimeEditHandler")
public class MemberTimeEditHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private MemberService memberService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        final String chatId = context.get("chatId");
        final int messageId = context.get("messageId");
        final String text = context.get("text");
        MemberEntity member = context.get("member");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == LocalChatStates.MEMBER_TIME) {
            bot.edit("Напишите время мероприятия\nПример: 16:42", chatId, messageId);
        }
        else if (previousState == LocalChatStates.MEMBER_TIME_EDIT) {
            try {
                member = memberService.setComfortTime(member, text);
                context.getHeaders().put("member", member);
            } catch (Exception e) {
                bot.send("Неверный формат", chatId);
                return;
            }

            context.setNextState(LocalChatStates.MEMBER_QUESTION);
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }

    }

}
