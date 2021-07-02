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

import java.util.Set;

@Component("localStartHandler")
public class LocalStartHandler implements MessageStateMachineHandler<LocalChatStates> {

    @Autowired
    private TelegramBot bot;

    @Autowired
    private MemberService memberService;

    @Override
    public void handle(MessageStateMachineContext<LocalChatStates> context) {
        final String chatId = context.get("chatId");
        final UserEntity user = context.get("user");

        final LocalChatStates previousState = context.getPreviousState();
        if (previousState == null) {
            final Set<EventEntity> events = memberService.getActualEventsByTelegramId(user.getTelegramId());
            for (EventEntity event : events) {
                final MemberEntity member = memberService.getMember(user, event);
                final MemberStatus status = member.getStatus();
                if (status == MemberStatus.WAIT_PRIVATE_MESSAGE) {
                    memberService.setStatus(member, MemberStatus.FILL_QUESTIONS);
                }
            }

            bot.send("Привет, меня зовут EVE! Я твой личный менеджер мероприятий.\n Мои основные задачи:\n " +
                            "- помогать организовывать мероприятие;\n" +
                            "- уведомлять участников о статуте мероприятия;\n" +
                            "- опрашивать участников мероприятия;\n" +
                            "- выводить статистику опросов;\n" +
                            "- составлять отчет по результатом опросов.\n" +
                            "\n" +
                            "Вот команды которыми ты можешь пользоваться:\n" +
                            "/new_event - Создать новое мероприятие\n" +
                            "/actual_events - Посмотреть актуальные мероприятия\n" +
                            "/manage_events - Управлять созданными мероприятиями",
                    chatId
            );
            context.setNextState(LocalChatStates.CHECK_ACTUAL_EVENTS);
        }
        else {
            throw new UnsupportedOperationException(previousState.name() + " -> " + context.getCurrentState());
        }
    }

}
