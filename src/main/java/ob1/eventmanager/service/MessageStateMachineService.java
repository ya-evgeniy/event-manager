package ob1.eventmanager.service;

import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.statemachine.group.GroupChatStates;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

public interface MessageStateMachineService {

    boolean hasLocal(long chatId);

    boolean hasGroup(long chatId);

    MessageStateMachine<LocalChatStates> createLocal(Chat chat, LocalChatStates initialState);

    MessageStateMachine<GroupChatStates> createGroup(Chat chat, GroupChatStates initialState);

    MessageStateMachine<LocalChatStates> getLocal(long chatId);

    MessageStateMachine<GroupChatStates> getGroup(long chatId);

}
