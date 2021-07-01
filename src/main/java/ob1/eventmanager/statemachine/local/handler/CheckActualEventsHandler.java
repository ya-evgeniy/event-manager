package ob1.eventmanager.statemachine.local.handler;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.statemachine.MessageStateMachineContext;
import ob1.eventmanager.statemachine.MessageStateMachineHandler;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.MemberStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ob1.eventmanager.utils.KeyboardUtils.buttonOf;

@Component("localCheckActualEventsHandler")
public class CheckActualEventsHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private MemberService memberService;

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        final int userId = context.get("userId");
        final UserEntity user = context.get("user");

        final Set<EventEntity> actualEvents = memberService.getActualEventsByTelegramId(userId).stream()
                .filter(event -> {
                    final MemberEntity member = memberService.getMember(user, event);
                    return member.getStatus() != MemberStatus.WAIT_PRIVATE_MESSAGE
                            && member.getStatus() != MemberStatus.LEAVE
                            && member.getStatus() != MemberStatus.CANCEL;
                })
                .collect(Collectors.toSet());

        if (!actualEvents.isEmpty()) {
            final SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(context.get("chatId"));
            sendMessage.setText("Ты еще не заполнил опросник некоторых мероприятий\nВыбери мероприятие чтобы заполнить опросник");

            final List<List<InlineKeyboardButton>> keyboard = actualEvents.stream()
                    .map(event -> buttonOf(event.getName(), "/answer_questions " + event.getId()))
                    .map(Collections::singletonList)
                    .collect(Collectors.toList());

            sendMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
            bot.send(sendMessage);
        }

        context.setNextState(LocalChatStates.WAIT_COMMANDS);
    }

}
