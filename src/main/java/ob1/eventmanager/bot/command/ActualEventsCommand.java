package ob1.eventmanager.bot.command;

import ob1.eventmanager.bot.TelegramBot;
import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.MemberStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ob1.eventmanager.utils.KeyboardUtils.buttonOf;

@LocalCommand("actual_events")
public class ActualEventsCommand implements LocalCommandHandler {

    @Autowired
    private MemberService memberService;

    @Autowired
    private TelegramBot bot;

    @Override
    public void handle(MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        String chatId = (String) headers.get("chatId");

        if (stateMachine.getCurrentState() != LocalChatStates.WAIT_COMMANDS) {
            bot.send(
                    "Похоже, что ты все еще не завершил предудыщее действие(почитай выше в чате). Как закончишь, выполни команду заново.",
                    chatId
            );
            return;
        }


        final UserEntity user = (UserEntity) headers.get("user");
        final int userId = user.getTelegramId();

        final Set<EventEntity> actualEvents = memberService.getActualEventsByTelegramId(userId).stream()
                .filter(event -> {
                    try {
                        final MemberEntity member = memberService.getMember(user, event);
                        return member.getStatus() != MemberStatus.WAIT_PRIVATE_MESSAGE
                                && member.getStatus() != MemberStatus.LEAVE
                                && member.getStatus() != MemberStatus.CANCEL;
                    }
                    catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toSet());

        if (actualEvents.isEmpty()) {
            bot.send("Актуальных событий нет", chatId);
            return;
        }

        final SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        if (actualEvents.size() == 1) {
            sendMessage.setText("У тебя есть 1 актуальное событие\nВыбери мероприятие, чтобы заполнить опросник");
        }
        else {
            sendMessage.setText("У тебя есть " + actualEvents.size() + " актуальных событий\nВыбери мероприятие, чтобы заполнить опросник");
        }

        final List<List<InlineKeyboardButton>> keyboard = actualEvents.stream()
                .map(event -> buttonOf(event.getName(), "/answer_questions " + event.getId()))
                .map(Collections::singletonList)
                .collect(Collectors.toList());

        sendMessage.setReplyMarkup(new InlineKeyboardMarkup(keyboard));
        bot.send(sendMessage);

        stateMachine.setCurrentState(LocalChatStates.WAIT_COMMANDS);
        stateMachine.handle(headers);
    }

}
