package ob1.eventmanager.statemachine.local.handler.member;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component("localMemberDateEditHandler")
public class MemberDateEditHandler implements MessageStateMachineHandler<LocalChatStates> {

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
        if (previousState == LocalChatStates.MEMBER_DATE) {
            bot.send("Введите дату, которая вас устраивает" +
                    "\nПример: 25.05.2021 или 25 мая 2021 (год писать не обязательно, возьмется текущий)", chatId);
        }
        else if (previousState == LocalChatStates.MEMBER_DATE_EDIT) {
            if (text == null) {
                bot.send("Напиши ответ текстом", chatId);
                return;
            }
            try {
                member = memberService.setComfortDate(member, text);
                context.getHeaders().put("member", member);
            } catch (Exception e) {
                bot.send("Неверный формат", chatId);
                return;
            }

            context.setNextState(LocalChatStates.MEMBER_TIME);
        } else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }

    }

}
