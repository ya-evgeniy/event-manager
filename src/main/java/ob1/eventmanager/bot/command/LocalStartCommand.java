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
        if (stateMachine.getCurrentState() != LocalChatStates.START) {
            return;
        }

        UserEntity user = (UserEntity) headers.get("user");
        user = userService.setUserChatId(user, (long) headers.get("chatIdLong"));
        headers.put("user", user);

        stateMachine.handle(headers);
    }

}
