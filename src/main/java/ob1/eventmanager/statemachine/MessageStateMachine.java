package ob1.eventmanager.statemachine;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

public class MessageStateMachine<T> {

    @Getter private final long id;
    private final Map<T, MessageStateMachineHandler<T>> stateHandlers;

    @Getter @Setter private T previousState;
    @Getter @Setter private T currentState;

    public MessageStateMachine(long id, T previousState, T currentState, Map<T, MessageStateMachineHandler<T>> stateHandlers) {
        this.id = id;
        this.previousState = previousState;
        this.currentState = currentState;
        this.stateHandlers = stateHandlers;
    }

    public void handle(Map<String, Object> headers) {
        MessageStateMachineHandler<T> handler = stateHandlers.get(this.currentState);
        if (handler == null) {
            throw new UnsupportedOperationException(String.format("Handler for state '%s' not found", this.currentState));
        }
        while (handler != null) {
            final MessageStateMachineContext<T> context = new MessageStateMachineContext<>(headers, this.currentState, this.previousState, null);
            System.out.println(context.get("chatId") + ": " + context.getPreviousState() + " -> " + context.getCurrentState());
            handler.handle(context);
            handler = null;

            final T nextState = context.getNextState();
            previousState = this.currentState;
            if (nextState != null) {
                this.currentState = nextState;
                handler = stateHandlers.get(this.currentState);
                if (handler == null) {
                    throw new UnsupportedOperationException(String.format("Handler for state '%s' not found", nextState));
                }
            }
        }
    }

}
