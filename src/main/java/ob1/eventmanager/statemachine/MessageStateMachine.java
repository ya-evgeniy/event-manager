package ob1.eventmanager.statemachine;

import lombok.Getter;

import java.util.Map;

public class MessageStateMachine<T> {

    @Getter private final String id;
    private final Map<T, MessageStateMachineHandler<T>> stateHandlers;

    @Getter private T previousState;
    @Getter private T state;

    public MessageStateMachine(String id, T state, Map<T, MessageStateMachineHandler<T>> stateHandlers) {
        this.id = id;
        this.state = state;
        this.stateHandlers = stateHandlers;
    }

    public void handle(Map<String, Object> headers) {
        MessageStateMachineHandler<T> handler = stateHandlers.get(this.state);
        while (handler != null) {
            final MessageStateMachineContext<T> context = new MessageStateMachineContext<>(headers, this.previousState, null);
            handler.handle(context);

            final T nextState = context.getNextState();
            if (nextState != null) {
                previousState = this.state;
                this.state = nextState;

                handler = stateHandlers.get(this.state);
            }
        }
    }

}
