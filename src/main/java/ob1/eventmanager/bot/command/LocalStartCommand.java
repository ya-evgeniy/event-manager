package ob1.eventmanager.bot.command;

import ob1.eventmanager.bot.ann.LocalCommand;
import ob1.eventmanager.entity.EventEntity;
import ob1.eventmanager.entity.MemberEntity;
import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.service.MemberService;
import ob1.eventmanager.service.UserService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.utils.MemberStatus;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Set;

@LocalCommand("start")
public class LocalStartCommand implements LocalCommandHandler {

    @Autowired
    private UserService userService;

    @Autowired
    private MemberService memberService;

    @Override
    public void handle(MessageStateMachine<LocalChatStates> stateMachine, Map<String, Object> headers) {
        UserEntity user = (UserEntity) headers.get("user");
        user = userService.setUserChatId(user, (long) headers.get("chatIdLong"));
        headers.put("user", user);

        final Set<EventEntity> events = memberService.getActualEventsByTelegramId(user.getTelegramId());
        for (EventEntity event : events) {
            final MemberEntity member = memberService.getMember(user, event);
            final MemberStatus status = member.getStatus();
            if (status == MemberStatus.WAIT_PRIVATE_MESSAGE) {
                memberService.setStatus(member, MemberStatus.FILL_QUESTIONS);
            }
        }

        stateMachine.handle(headers);
    }

}
