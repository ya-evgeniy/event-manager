package ob1.eventmanager.statemachine;

import ob1.eventmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import java.util.HashMap;
import java.util.Map;

public class MessageStateMachineFactory<T> {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private UserService userService;

    private MessageStateMachineFactory() {
        this(new HashMap<>());
    }

    private final Map<T, MessageStateMachineHandler<T>> stateHandlers;

    private MessageStateMachineFactory(Map<T, MessageStateMachineHandler<T>> stateHandlers) {
        this.stateHandlers = stateHandlers;
    }

    public MessageStateMachine<T> create(long id, T previousState, T currentState) {
        return new MessageStateMachine<>(id, previousState, currentState, this.stateHandlers);
    }

    public static <T> Builder<T> builder(Class<T> type) {
        return new Builder<>();
    }

    public static class Builder<T> {

        private final Map<T, MessageStateMachineHandler<T>> stateHandlers = new HashMap<>();

        public Builder<T> node(T node, MessageStateMachineHandler<T> handler) {
            stateHandlers.put(node, handler);
            return this;
        }

        public MessageStateMachineFactory<T> build() {
            return new MessageStateMachineFactory<>(this.stateHandlers);
        }

    }

}
