package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.service.MemberAnswerService;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("localMemberPlaceEditHandler")
public class MemberPlaceEditHandler implements MessageStateMachineHandler<LocalChatStates> {

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
        if (previousState == LocalChatStates.MEMBER_PLACE) {
            bot.edit("Введи название места, которое тебя устраивает", chatId, messageId);
        }
        else if (previousState == LocalChatStates.MEMBER_PLACE_EDIT) {
            member = memberService.setComfortPlace(member, text);
            context.getHeaders().put("member", member);

            context.setNextState(LocalChatStates.MEMBER_DATE);
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }

    }

}
