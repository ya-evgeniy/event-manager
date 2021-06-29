package ob1.eventmanager.service;

import ob1.eventmanager.entity.UserEntity;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.group.GroupChatStates;
import ob1.eventmanager.statemachine.local.LocalChatStates;

public interface MessageStateMachineService {

    MessageStateMachine<LocalChatStates> createLocal(UserEntity user);

    MessageStateMachine<GroupChatStates> getGroup(long chatId);

    void save(UserEntity userEntity, MessageStateMachine<LocalChatStates> stateMachine);

}
