package ob1.eventmanager.service.impl;

import ob1.eventmanager.service.MessageStateMachineService;
import ob1.eventmanager.statemachine.MessageStateMachine;
import ob1.eventmanager.statemachine.MessageStateMachineFactory;
import ob1.eventmanager.statemachine.local.LocalChatStates;
import ob1.eventmanager.statemachine.group.GroupChatStates;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.HashMap;
import java.util.Map;

@Service
public class MessageStateMachineServiceImpl implements MessageStateMachineService {

    @Autowired @Qualifier("localStateMachineFactory")
    private MessageStateMachineFactory<LocalChatStates> localStateMachineFactory;

    @Autowired @Qualifier("groupStateMachineFactory")
    private MessageStateMachineFactory<GroupChatStates> groupStateMachineFactory;

    private final Map<Long, MessageStateMachine<LocalChatStates>> localStateMachines = new HashMap<>();

    private final Map<Long, MessageStateMachine<GroupChatStates>> groupStateMachines = new HashMap<>();


    @Override
    public boolean hasLocal(long chatId) {
        return localStateMachines.containsKey(chatId);
    }

    @Override
    public boolean hasGroup(long chatId) {
        return groupStateMachines.containsKey(chatId);
    }

    @Override
    public MessageStateMachine<LocalChatStates> createLocal(Chat chat, LocalChatStates initialState) {
        final MessageStateMachine<LocalChatStates> machine = localStateMachineFactory.create(
                String.valueOf(chat.getId()),
                initialState
        );

        this.localStateMachines.put(chat.getId(), machine);

        return machine;
    }

    @Override
    public MessageStateMachine<GroupChatStates> createGroup(Chat chat, GroupChatStates initialState) {
        final MessageStateMachine<GroupChatStates> machine = groupStateMachineFactory.create(
                String.valueOf(chat.getId()),
                initialState
        );

        this.groupStateMachines.put(chat.getId(), machine);

        return machine;
    }

    @Override
    public MessageStateMachine<LocalChatStates> getLocal(long chatId) {
        final MessageStateMachine<LocalChatStates> machine = this.localStateMachines.get(chatId);
        if (machine == null) {
            throw new UnsupportedOperationException();
        }
        return machine;
    }

    @Override
    public MessageStateMachine<GroupChatStates> getGroup(long chatId) {
        final MessageStateMachine<GroupChatStates> machine = this.groupStateMachines.get(chatId);
        if (machine == null) {
            throw new UnsupportedOperationException();
        }
        return machine;
    }
}
