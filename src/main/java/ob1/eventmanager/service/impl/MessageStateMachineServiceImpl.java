package ob1.eventmanager.service.impl;

import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.service.MessageStateMachineService;
import ob1.eventmanager.service.UserService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.MessageStateMachineFactory;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.statemachine.group.GroupChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class MessageStateMachineServiceImpl implements MessageStateMachineService {

    @Autowired @Qualifier("localStateMachineFactory")
    private MessageStateMachineFactory<LocalChatStates> localStateMachineFactory;

//    @Autowired @Qualifier("groupStateMachineFactory")
//    private MessageStateMachineFactory<GroupChatStates> groupStateMachineFactory;

    @Autowired
    private UserService userService;

    @Override
    public MessageStateMachine<LocalChatStates> createLocal(UserEntity user) {
        final String userPreviousState = user.getPreviousState();
        final String userCurrentState = user.getPreviousState();

        final LocalChatStates previousState = LocalChatStates.getByName(userPreviousState).orElse(null);
        final LocalChatStates currentState = LocalChatStates.getByName(userCurrentState).orElse(LocalChatStates.START);
        return this.localStateMachineFactory.create(
                user.getTelegramId(),
                previousState,
                currentState
        );
    }

    @Override
    public MessageStateMachine<GroupChatStates> getGroup(long chatId) {
        throw new UnsupportedOperationException("not impl yet");
    }

    @Override
    public void save(UserEntity userEntity, MessageStateMachine<LocalChatStates> stateMachine) {
        String previousState = stateMachine.getPreviousState() == null ? null : stateMachine.getPreviousState().name();
        String currentState = stateMachine.getCurrentState() == null ? null : stateMachine.getCurrentState().name();
        userService.setUserState(userEntity, previousState, currentState);
    }

}
