package ob1.eventmanager.statemachine;

import java.util.HashMap;
import java.util.Map;

public class MessageStateMachineFactory<T> {

    private final Map<T, MessageStateMachineHandler<T>> stateHandlers;

    private MessageStateMachineFactory() {
        this(new HashMap<>());
    }

    private MessageStateMachineFactory(Map<T, MessageStateMachineHandler<T>> stateHandlers) {
        this.stateHandlers = stateHandlers;
    }

    public MessageStateMachine<T> create(String id, T state) {
        return new MessageStateMachine<>(id, state, this.stateHandlers);
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
